const BASE_URL = 'http://localhost:8080/api';

let currentPage = 1;
let currentKeyword = '';
let currentCategory = '';
let currentUserId = null;
let currentProduct = null;
let selectedImages = [];

document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadHomeProducts();
    checkLogin();

    document.getElementById('login-form').addEventListener('submit', handleLogin);
    document.getElementById('register-form').addEventListener('submit', handleRegister);
    document.getElementById('publish-form').addEventListener('submit', handlePublish);
    document.getElementById('change-password-form').addEventListener('submit', handleChangePassword);
    document.getElementById('publish-images').addEventListener('change', handleImageSelect);
    
    window.addEventListener('popstate', (e) => {
        if (e.state && e.state.page) {
            showPage(e.state.page);
        } else {
            showPage('home');
        }
    });
});

function getAuthHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    if (currentUserId) {
        headers['X-User-Id'] = currentUserId;
    }
    return headers;
}

function convertImageUrl(url) {
    if (!url) return 'https://via.placeholder.com/280x200?text=商品图片';
    if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
    }
    return 'http://localhost:8080' + url;
}

async function loadCategories() {
    try {
        const response = await fetch(`${BASE_URL}/categories`);
        const data = await response.json();
        if (data.code === 200) {
            const select = document.getElementById('category-select');
            const publishSelect = document.getElementById('publish-category');
            data.data.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.id;
                option.textContent = cat.name;
                select.appendChild(option.cloneNode(true));
                publishSelect.appendChild(option);
            });
        }
    } catch (error) {
        console.error('加载分类失败:', error);
    }
}

async function loadHomeProducts() {
    try {
        const response = await fetch(`${BASE_URL}/products?page=1&size=6`);
        const data = await response.json();
        if (data.code === 200) {
            renderProducts(data.data.content, 'home-products');
        }
    } catch (error) {
        console.error('加载商品失败:', error);
    }
}

async function loadProducts(page = 1, keyword = '', categoryId = '') {
    try {
        let url = `${BASE_URL}/products?page=${page}&size=8`;
        if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
        if (categoryId) url += `&categoryId=${categoryId}`;
        
        const response = await fetch(url);
        const data = await response.json();
        if (data.code === 200) {
            renderProducts(data.data.content, 'product-list');
            renderPagination(data.data);
        }
    } catch (error) {
        console.error('加载商品失败:', error);
    }
}

function renderProducts(products, containerId) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';
    
    if (!products || products.length === 0) {
        container.innerHTML = '<p style="text-align:center; color:#999;">暂无商品</p>';
        return;
    }

    products.forEach(product => {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.onclick = () => showProductDetail(product.id);
        
        const images = product.images ? JSON.parse(product.images) : [];
        const imgSrc = images.length > 0 ? convertImageUrl(images[0]) : 'https://via.placeholder.com/280x200?text=商品图片';
        
        card.innerHTML = `
            <img src="${imgSrc}" alt="${product.name}">
            <div class="product-info">
                <h3>${product.name}</h3>
                <div class="product-price">¥${product.price}</div>
                ${product.originalPrice ? `<div class="product-original-price">¥${product.originalPrice}</div>` : ''}
                <div class="product-meta">浏览: ${product.views} | 库存: ${product.stock}</div>
            </div>
        `;
        container.appendChild(card);
    });
}

function renderPagination(pageData) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';
    
    const totalPages = pageData.totalPages;
    const current = pageData.number + 1;
    
    if (current > 1) {
        const prevBtn = document.createElement('button');
        prevBtn.textContent = '上一页';
        prevBtn.onclick = () => {
            currentPage--;
            loadProducts(currentPage, currentKeyword, currentCategory);
        };
        pagination.appendChild(prevBtn);
    }
    
    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement('button');
        btn.textContent = i;
        if (i === current) btn.className = 'active';
        btn.onclick = () => {
            currentPage = i;
            loadProducts(currentPage, currentKeyword, currentCategory);
        };
        pagination.appendChild(btn);
    }
    
    if (current < totalPages) {
        const nextBtn = document.createElement('button');
        nextBtn.textContent = '下一页';
        nextBtn.onclick = () => {
            currentPage++;
            loadProducts(currentPage, currentKeyword, currentCategory);
        };
        pagination.appendChild(nextBtn);
    }
}

async function showProductDetail(productId) {
    try {
        const response = await fetch(`${BASE_URL}/products/${productId}`);
        const data = await response.json();
        if (data.code === 200) {
            currentProduct = data.data;
            const content = document.getElementById('detail-content');
            const images = currentProduct.images ? JSON.parse(currentProduct.images) : [];
            const imgSrc = images.length > 0 ? convertImageUrl(images[0]) : 'https://via.placeholder.com/400x400?text=商品图片';
            
            content.innerHTML = `
                <div class="detail-content">
                    <img src="${imgSrc}" alt="${currentProduct.name}">
                    <div class="detail-info">
                        <h2>${currentProduct.name}</h2>
                        <div class="detail-price">¥${currentProduct.price}</div>
                        ${currentProduct.originalPrice ? `<div class="product-original-price">原价: ¥${currentProduct.originalPrice}</div>` : ''}
                        <div class="detail-meta">
                            <p>分类: ${currentProduct.categoryId || '未分类'}</p>
                            <p>浏览量: ${currentProduct.views}</p>
                            <p>库存: ${currentProduct.stock}</p>
                        </div>
                        <div class="detail-desc">${currentProduct.description || '暂无描述'}</div>
                        <div class="detail-actions">
                            <button class="btn-buy" onclick="buyProduct()">立即购买</button>
                            <button class="btn-message" onclick="openMessageModal()">联系卖家</button>
                        </div>
                    </div>
                </div>
                <h3>商品评价</h3>
                <div id="product-reviews"></div>
            `;
            showPage('product-detail');
            loadProductReviews(productId);
        }
    } catch (error) {
        console.error('加载商品详情失败:', error);
    }
}

async function loadProductReviews(productId) {
    try {
        const response = await fetch(`${BASE_URL}/reviews/product/${productId}`);
        const data = await response.json();
        if (data.code === 200) {
            const container = document.getElementById('product-reviews');
            if (!data.data || data.data.length === 0) {
                container.innerHTML = '<p style="color:#999;">暂无评价</p>';
                return;
            }
            container.innerHTML = data.data.map(r => `
                <div class="review-item">
                    <div class="review-rating">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</div>
                    <div class="review-content">${r.content || '暂无评价内容'}</div>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('加载评价失败:', error);
    }
}

function searchProducts() {
    currentKeyword = document.getElementById('search-input').value;
    currentPage = 1;
    loadProducts(currentPage, currentKeyword, currentCategory);
    showPage('products');
}

function filterByCategory() {
    currentCategory = document.getElementById('category-select').value;
    currentPage = 1;
    loadProducts(currentPage, currentKeyword, currentCategory);
    showPage('products');
}

function showPage(pageId) {
    document.querySelectorAll('.page').forEach(page => {
        page.style.display = 'none';
    });
    document.getElementById(pageId).style.display = 'block';
    
    if (window.location.hash !== '#' + pageId) {
        window.history.pushState({ page: pageId }, '', '#' + pageId);
    }
    
    if (pageId === 'products') {
        currentPage = 1;
        loadProducts(currentPage, currentKeyword, currentCategory);
    } else if (pageId === 'profile' && currentUserId) {
        loadProfile();
    }
}

async function checkLogin() {
    const savedUserId = localStorage.getItem('userId');
    if (savedUserId) {
        currentUserId = parseInt(savedUserId);
        updateUIForLoggedInUser({
            id: currentUserId,
            username: localStorage.getItem('username'),
            nickname: localStorage.getItem('nickname')
        });
        return;
    }
    
    try {
        const response = await fetch(`${BASE_URL}/users/current`, {
            credentials: 'include'
        });
        const data = await response.json();
        if (data.code === 200) {
            currentUserId = data.data.id;
            localStorage.setItem('userId', data.data.id);
            localStorage.setItem('username', data.data.username);
            localStorage.setItem('nickname', data.data.nickname || data.data.username);
            updateUIForLoggedInUser(data.data);
        }
    } catch (error) {
        console.log('未登录');
    }
}

function updateUIForLoggedInUser(user) {
    document.getElementById('nickname').textContent = user.nickname || user.username;
    document.getElementById('user-name').style.display = 'inline';
    document.getElementById('login-btn').style.display = 'none';
    document.getElementById('register-btn').style.display = 'none';
    document.getElementById('logout-btn').style.display = 'inline';
    document.getElementById('profile-btn').style.display = 'inline';
    
    if (user.role === 1) {
        document.getElementById('admin-btn').style.display = 'inline';
    } else {
        document.getElementById('admin-btn').style.display = 'none';
    }
}

function clearLoginState() {
    currentUserId = null;
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    localStorage.removeItem('nickname');
    document.getElementById('user-name').style.display = 'none';
    document.getElementById('login-btn').style.display = 'inline';
    document.getElementById('register-btn').style.display = 'inline';
    document.getElementById('logout-btn').style.display = 'none';
    document.getElementById('profile-btn').style.display = 'none';
}

async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    
    try {
        const response = await fetch(`${BASE_URL}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
            credentials: 'include'
        });
        const data = await response.json();
        if (data.code === 200) {
            currentUserId = data.data.id;
            localStorage.setItem('userId', data.data.id);
            localStorage.setItem('username', data.data.username);
            localStorage.setItem('nickname', data.data.nickname || data.data.username);
            localStorage.setItem('role', data.data.role || 0);
            document.getElementById('login-error').style.display = 'none';
            alert('登录成功');
            updateUIForLoggedInUser(data.data);
            showPage('home');
        } else {
            document.getElementById('login-error').textContent = data.message;
            document.getElementById('login-error').style.display = 'block';
        }
    } catch (error) {
        document.getElementById('login-error').textContent = '登录失败: ' + error.message;
        document.getElementById('login-error').style.display = 'block';
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const username = document.getElementById('reg-username').value;
    const password = document.getElementById('reg-password').value;
    const phone = document.getElementById('reg-phone').value;
    const email = document.getElementById('reg-email').value;
    const nickname = document.getElementById('reg-nickname').value;
    
    try {
        const response = await fetch(`${BASE_URL}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, phone, email, nickname })
        });
        const data = await response.json();
        if (data.code === 200) {
            document.getElementById('register-error').style.display = 'none';
            alert('注册成功，请登录');
            showPage('login');
        } else {
            document.getElementById('register-error').textContent = data.message;
            document.getElementById('register-error').style.display = 'block';
        }
    } catch (error) {
        document.getElementById('register-error').textContent = '注册失败: ' + error.message;
        document.getElementById('register-error').style.display = 'block';
    }
}

async function handlePublish(e) {
    e.preventDefault();
    if (!currentUserId) {
        const savedUserId = localStorage.getItem('userId');
        if (savedUserId) {
            currentUserId = parseInt(savedUserId);
        } else {
            alert('请先登录');
            showPage('login');
            return;
        }
    }
    
    const name = document.getElementById('publish-name').value;
    const description = document.getElementById('publish-desc').value;
    const price = parseFloat(document.getElementById('publish-price').value);
    const originalPrice = parseFloat(document.getElementById('publish-original-price').value) || null;
    const categoryId = parseInt(document.getElementById('publish-category').value) || null;
    const stock = parseInt(document.getElementById('publish-stock').value);
    
    let images = [];
    
    if (selectedImages.length > 0) {
        try {
            const formData = new FormData();
            selectedImages.forEach((file, index) => {
                formData.append('files', file);
            });
            
            const uploadResponse = await fetch(`${BASE_URL}/files/upload/multiple`, {
                method: 'POST',
                body: formData
            });
            const uploadData = await uploadResponse.json();
            if (uploadData.code === 200) {
                images = uploadData.data;
            } else {
                alert('图片上传失败: ' + uploadData.message);
                return;
            }
        } catch (error) {
            alert('图片上传失败: ' + error.message);
            return;
        }
    }
    
    try {
        const response = await fetch(`${BASE_URL}/products`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ 
                name, 
                description, 
                price, 
                originalPrice, 
                categoryId, 
                stock,
                images: JSON.stringify(images)
            })
        });
        const data = await response.json();
        if (data.code === 200) {
            document.getElementById('publish-error').style.display = 'none';
            alert('发布成功');
            selectedImages = [];
            document.getElementById('upload-preview').innerHTML = '';
            loadHomeProducts();
            showPage('home');
        } else if (data.code === 401) {
            alert('登录已过期，请重新登录');
            clearLoginState();
            showPage('login');
        } else {
            document.getElementById('publish-error').textContent = data.message;
            document.getElementById('publish-error').style.display = 'block';
        }
    } catch (error) {
        document.getElementById('publish-error').textContent = '发布失败: ' + error.message;
        document.getElementById('publish-error').style.display = 'block';
    }
}

async function logout() {
    try {
        await fetch(`${BASE_URL}/users/logout`, { credentials: 'include' });
    } catch (error) {
        console.error('退出失败:', error);
    }
    clearLoginState();
    alert('退出成功');
    showPage('home');
}

async function loadProfile() {
    try {
        const [userRes, productsRes, reviewsRes, messagesRes] = await Promise.all([
            fetch(`${BASE_URL}/users/${currentUserId}`),
            fetch(`${BASE_URL}/products/seller/${currentUserId}`),
            fetch(`${BASE_URL}/reviews/user/${currentUserId}`),
            fetch(`${BASE_URL}/messages/receiver`, { headers: getAuthHeaders() })
        ]);
        
        const userData = await userRes.json();
        const productsData = await productsRes.json();
        const reviewsData = await reviewsRes.json();
        const messagesData = await messagesRes.json();
        
        if (userData.code === 200) {
            const user = userData.data;
            document.getElementById('user-profile').innerHTML = `
                <p><strong>用户名:</strong> ${user.username}</p>
                <p><strong>昵称:</strong> ${user.nickname || '未设置'}</p>
                <p><strong>手机号:</strong> ${user.phone}</p>
                <p><strong>邮箱:</strong> ${user.email || '未设置'}</p>
                <p><strong>地址:</strong> ${user.address || '未设置'}</p>
            `;
        }
        
        if (productsData.code === 200) {
            renderMyProducts(productsData.data);
        }
        
        loadOrders('buyer');
        
        if (reviewsData.code === 200) {
            renderReviews(reviewsData.data);
        }
        
        if (messagesData.code === 200) {
            renderMessages(messagesData.data);
        }
    } catch (error) {
        console.error('加载个人信息失败:', error);
    }
}

async function loadOrders(type) {
    const tabs = document.querySelectorAll('.order-tabs button');
    tabs.forEach(tab => tab.classList.remove('tab-active'));
    
    const url = type === 'buyer' ? `${BASE_URL}/orders/user` : `${BASE_URL}/orders/seller`;
    
    try {
        const response = await fetch(url, { headers: getAuthHeaders() });
        const data = await response.json();
        
        if (data.code === 200) {
            renderOrders(data.data, type);
            document.querySelector(`.order-tabs button:nth-child(${type === 'buyer' ? '1' : '2'})`).classList.add('tab-active');
        }
    } catch (error) {
        console.error('加载订单失败:', error);
    }
}

async function renderOrders(orders, type = 'buyer') {
    const container = document.getElementById('my-orders');
    if (!orders || orders.length === 0) {
        container.innerHTML = '<p style="color:#999;">暂无订单</p>';
        return;
    }
    
    const statusMap = {
        1: { text: '待付款', class: 'status-pending' },
        2: { text: '已付款', class: 'status-paid' },
        3: { text: '已发货', class: 'status-shipped' },
        4: { text: '已完成', class: 'status-completed' },
        5: { text: '已取消', class: 'status-canceled' }
    };
    
    const productMap = {};
    for (const order of orders) {
        if (!productMap[order.productId]) {
            try {
                const response = await fetch(`${BASE_URL}/products/${order.productId}`);
                const data = await response.json();
                if (data.code === 200) {
                    productMap[order.productId] = data.data;
                }
            } catch (error) {
                console.error('获取商品信息失败:', error);
            }
        }
    }
    
    container.innerHTML = orders.map(order => {
        const product = productMap[order.productId];
        const status = statusMap[order.status] || { text: '未知', class: 'status-pending' };
        
        let actions = '';
        if (type === 'buyer') {
            actions = `
                ${order.status === 1 ? `<button onclick="showPayment('${order.id}')">去支付</button>` : ''}
                ${order.status === 1 ? `<button onclick="cancelOrder('${order.id}')">取消订单</button>` : ''}
                ${order.status === 4 ? `<button onclick="reviewOrder('${order.id}')">评价商品</button>` : ''}
            `;
        } else {
            actions = `
                ${order.status === 2 ? `<button onclick="shipOrder('${order.id}')">发货</button>` : ''}
                ${order.status === 3 ? `<button onclick="completeOrder('${order.id}')">确认完成</button>` : ''}
            `;
        }
        
        const productName = product ? product.name : '未知商品';
        const productImages = product && product.images ? JSON.parse(product.images) : [];
        const firstImage = productImages.length > 0 ? convertImageUrl(productImages[0]) : 'https://via.placeholder.com/80x80?text=商品';
        
        return `
            <div class="order-item">
                <div class="order-header">
                    <span>订单号: ${order.id}</span>
                    <span class="order-status ${status.class}">${status.text}</span>
                </div>
                <div class="order-product">
                    <img src="${firstImage}" alt="${productName}" style="width:80px;height:80px;object-fit:cover;">
                    <div class="order-product-info">
                        <h4>${productName}</h4>
                        <p>单价: ¥${order.price} x ${order.quantity}</p>
                    </div>
                </div>
                <div class="order-total">合计: ¥${order.totalAmount}</div>
                <div class="order-actions">
                    ${actions}
                </div>
            </div>
        `;
    }).join('');
}

function renderReviews(reviews) {
    const container = document.getElementById('my-reviews');
    if (!reviews || reviews.length === 0) {
        container.innerHTML = '<p style="color:#999;">暂无评价</p>';
        return;
    }
    
    container.innerHTML = reviews.map(r => `
        <div class="review-item">
            <div class="review-rating">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</div>
            <div class="review-content">${r.content || '暂无评价内容'}</div>
        </div>
    `).join('');
}

function renderMessages(messages) {
    const container = document.getElementById('my-messages');
    if (!messages || messages.length === 0) {
        container.innerHTML = '<p style="color:#999;">暂无留言</p>';
        return;
    }
    
    container.innerHTML = messages.map(msg => {
        const isRead = msg.read ? 'read' : 'unread';
        const time = new Date(msg.createdAt).toLocaleString();
        return `
            <div class="message-item ${isRead}">
                <div class="message-header">
                    <span class="message-sender">发送者: ${msg.senderNickname || '未知用户'}</span>
                    <span class="message-time">${time}</span>
                </div>
                <div class="message-content">${msg.content}</div>
                <div class="message-product">关联商品ID: ${msg.productId}</div>
                <div class="message-actions">
                    <button onclick="replyMessage('${msg.id}', '${msg.senderNickname || '未知用户'}')">回复</button>
                </div>
            </div>
        `;
    }).join('');
}

function replyMessage(messageId, senderName) {
    const content = prompt(`回复 ${senderName}：`);
    if (!content || !content.trim()) {
        return;
    }
    
    fetch(`${BASE_URL}/messages/${messageId}/reply`, {
        method: 'POST',
        headers: {
            ...getAuthHeaders(),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(content)
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('回复成功');
            loadProfile();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        alert('回复失败: ' + error.message);
    });
}

function loadAdminUsers() {
    const tabs = document.querySelectorAll('.admin-tabs button');
    tabs.forEach(tab => tab.classList.remove('tab-active'));
    document.querySelector('.admin-tabs button:nth-child(1)').classList.add('tab-active');
    
    fetch(`${BASE_URL}/admin/users`, { headers: getAuthHeaders() })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                renderAdminUsers(data.data);
            } else if (data.code === 403) {
                alert('权限不足');
                showPage('profile');
            }
        });
}

function renderAdminUsers(users) {
    const container = document.getElementById('admin-content');
    if (!users || users.length === 0) {
        container.innerHTML = '<p style="color:#999;">暂无用户</p>';
        return;
    }
    
    container.innerHTML = `
        <table class="admin-table">
            <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>昵称</th>
                <th>手机号</th>
                <th>角色</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            ${users.map(user => `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.nickname || '-'}</td>
                    <td>${user.phone}</td>
                    <td>${user.role === 1 ? '管理员' : '普通用户'}</td>
                    <td>${user.status === 1 ? '正常' : '禁用'}</td>
                    <td class="admin-actions">
                        <button class="btn-active" onclick="toggleUserStatus(${user.id}, ${user.status})">
                            ${user.status === 1 ? '禁用' : '启用'}
                        </button>
                        <button class="btn-delete" onclick="deleteAdminUser(${user.id})">删除</button>
                    </td>
                </tr>
            `).join('')}
        </table>
    `;
}

function toggleUserStatus(userId, currentStatus) {
    const newStatus = currentStatus === 1 ? 0 : 1;
    fetch(`${BASE_URL}/admin/users/${userId}/status?status=${newStatus}`, {
        method: 'PUT',
        headers: getAuthHeaders()
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('状态更新成功');
            loadAdminUsers();
        } else {
            alert(data.message);
        }
    });
}

function deleteAdminUser(userId) {
    if (!confirm('确定删除该用户？')) return;
    fetch(`${BASE_URL}/admin/users/${userId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('删除成功');
            loadAdminUsers();
        } else {
            alert(data.message);
        }
    });
}

function loadAdminProducts() {
    const tabs = document.querySelectorAll('.admin-tabs button');
    tabs.forEach(tab => tab.classList.remove('tab-active'));
    document.querySelector('.admin-tabs button:nth-child(2)').classList.add('tab-active');
    
    fetch(`${BASE_URL}/admin/products`, { headers: getAuthHeaders() })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                renderAdminProducts(data.data);
            } else if (data.code === 403) {
                alert('权限不足');
                showPage('profile');
            }
        });
}

function renderAdminProducts(products) {
    const container = document.getElementById('admin-content');
    if (!products || products.length === 0) {
        container.innerHTML = '<p style="color:#999;">暂无商品</p>';
        return;
    }
    
    container.innerHTML = `
        <table class="admin-table">
            <tr>
                <th>ID</th>
                <th>名称</th>
                <th>价格</th>
                <th>卖家</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            ${products.map(product => `
                <tr>
                    <td>${product.id}</td>
                    <td>${product.name}</td>
                    <td>¥${product.price}</td>
                    <td>${product.sellerId}</td>
                    <td>${product.status === 1 ? '在售' : (product.status === 2 ? '下架' : '删除')}</td>
                    <td class="admin-actions">
                        <button class="btn-delete" onclick="deleteAdminProduct(${product.id})">删除</button>
                    </td>
                </tr>
            `).join('')}
        </table>
    `;
}

function deleteAdminProduct(productId) {
    if (!confirm('确定删除该商品？')) return;
    fetch(`${BASE_URL}/admin/products/${productId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('删除成功');
            loadAdminProducts();
        } else {
            alert(data.message);
        }
    });
}

function loadAdminOrders() {
    const tabs = document.querySelectorAll('.admin-tabs button');
    tabs.forEach(tab => tab.classList.remove('tab-active'));
    document.querySelector('.admin-tabs button:nth-child(3)').classList.add('tab-active');
    
    fetch(`${BASE_URL}/admin/orders`, { headers: getAuthHeaders() })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                renderAdminOrders(data.data);
            } else if (data.code === 403) {
                alert('权限不足');
                showPage('profile');
            }
        });
}

function renderAdminOrders(orders) {
    const container = document.getElementById('admin-content');
    if (!orders || orders.length === 0) {
        container.innerHTML = '<p style="color:#999;">暂无订单</p>';
        return;
    }
    
    const statusMap = {
        1: '待付款', 2: '已付款', 3: '已发货', 4: '已完成', 5: '已取消'
    };
    
    container.innerHTML = `
        <table class="admin-table">
            <tr>
                <th>订单号</th>
                <th>商品ID</th>
                <th>买家</th>
                <th>卖家</th>
                <th>总价</th>
                <th>状态</th>
            </tr>
            ${orders.map(order => `
                <tr>
                    <td>${order.id}</td>
                    <td>${order.productId}</td>
                    <td>${order.buyerId}</td>
                    <td>${order.sellerId}</td>
                    <td>¥${order.totalAmount}</td>
                    <td>${statusMap[order.status] || '未知'}</td>
                </tr>
            `).join('')}
        </table>
    `;
}

function loadAdminStats() {
    const tabs = document.querySelectorAll('.admin-tabs button');
    tabs.forEach(tab => tab.classList.remove('tab-active'));
    document.querySelector('.admin-tabs button:nth-child(4)').classList.add('tab-active');
    
    fetch(`${BASE_URL}/admin/stats`, { headers: getAuthHeaders() })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                renderAdminStats(data.data);
            } else if (data.code === 403) {
                alert('权限不足');
                showPage('profile');
            }
        });
}

function renderAdminStats(stats) {
    const container = document.getElementById('admin-content');
    container.innerHTML = `
        <div class="stats-card">
            <div class="number">${stats.userCount}</div>
            <div class="label">用户总数</div>
        </div>
        <div class="stats-card">
            <div class="number">${stats.productCount}</div>
            <div class="label">商品总数</div>
        </div>
        <div class="stats-card">
            <div class="number">${stats.orderCount}</div>
            <div class="label">订单总数</div>
        </div>
    `;
}

function renderMyProducts(products) {
    const container = document.getElementById('my-products');
    if (!products || products.length === 0) {
        container.innerHTML = '<p style="color:#999;">暂无商品</p>';
        return;
    }
    
    const statusMap = {
        1: { text: '在售', class: 'status-online' },
        2: { text: '下架', class: 'status-offline' },
        3: { text: '删除', class: 'status-deleted' }
    };
    
    container.innerHTML = products.map(product => {
        const images = product.images ? JSON.parse(product.images) : [];
        const imgSrc = images.length > 0 ? convertImageUrl(images[0]) : 'https://via.placeholder.com/80x80?text=商品';
        const status = statusMap[product.status] || { text: '未知', class: 'status-online' };
        
        return `
            <div class="my-product-item">
                <img src="${imgSrc}" alt="${product.name}">
                <div class="my-product-info">
                    <h4>${product.name}</h4>
                    <p>价格: ¥${product.price}</p>
                    <span class="product-status ${status.class}">${status.text}</span>
                </div>
                <div class="my-product-actions">
                    ${product.status === 1 ? `<button onclick="offlineProduct(${product.id})">下架</button>` : ''}
                    ${product.status === 2 ? `<button onclick="onlineProduct(${product.id})">上架</button>` : ''}
                </div>
            </div>
        `;
    }).join('');
}

async function offlineProduct(productId) {
    try {
        const response = await fetch(`${BASE_URL}/products/${productId}/offline`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('下架成功');
            loadProfile();
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('下架失败: ' + error.message);
    }
}

async function onlineProduct(productId) {
    try {
        const response = await fetch(`${BASE_URL}/products/${productId}/online`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('上架成功');
            loadProfile();
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('上架失败: ' + error.message);
    }
}

async function buyProduct() {
    if (!currentUserId) {
        const savedUserId = localStorage.getItem('userId');
        if (savedUserId) {
            currentUserId = parseInt(savedUserId);
        } else {
            alert('请先登录');
            showPage('login');
            return;
        }
    }
    
    if (!currentProduct) return;
    
    try {
        const response = await fetch(`${BASE_URL}/orders`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ 
                productId: currentProduct.id, 
                quantity: 1,
                shippingAddress: '默认地址'
            })
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('下单成功，请支付');
            showPage('profile');
        } else if (data.code === 401) {
            alert('登录已过期，请重新登录');
            clearLoginState();
            showPage('login');
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('下单失败: ' + error.message);
    }
}

async function payOrder(orderId) {
    try {
        const response = await fetch(`${BASE_URL}/orders/${orderId}/pay?paymentMethod=微信支付`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('支付成功');
            showPage('profile');
            loadOrders('buyer');
        } else if (data.code === 401) {
            alert('登录已过期，请重新登录');
            clearLoginState();
            showPage('login');
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('支付失败: ' + error.message);
    }
}

function showPayment(orderId) {
    showPage('payment');
    
    fetch(`${BASE_URL}/orders/${orderId}`)
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                const order = data.data;
                document.getElementById('payment-content').innerHTML = `
                    <div class="payment-info">
                        <h3>订单信息</h3>
                        <p><strong>订单号:</strong> ${order.id}</p>
                        <p><strong>商品ID:</strong> ${order.productId}</p>
                        <p><strong>数量:</strong> ${order.quantity}</p>
                        <p><strong>单价:</strong> ¥${order.price}</p>
                        <p><strong>订单总价:</strong> <span class="payment-total">¥${order.totalAmount}</span></p>
                    </div>
                    <div class="payment-methods">
                        <h3>支付方式</h3>
                        <label><input type="radio" name="paymentMethod" value="微信支付" checked> 微信支付</label>
                        <label><input type="radio" name="paymentMethod" value="支付宝"> 支付宝</label>
                        <label><input type="radio" name="paymentMethod" value="银行卡"> 银行卡</label>
                    </div>
                    <div class="payment-actions">
                        <button class="btn-pay" onclick="confirmPayment('${order.id}')">确认支付</button>
                        <button onclick="showPage('profile')">取消</button>
                    </div>
                `;
            }
        });
}

function confirmPayment(orderId) {
    const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
    
    fetch(`${BASE_URL}/orders/${orderId}/pay?paymentMethod=${paymentMethod}`, {
        method: 'PUT',
        headers: getAuthHeaders()
    })
    .then(response => response.json())
    .then(data => {
        if (data.code === 200) {
            alert('支付成功！');
            showPage('profile');
            loadOrders('buyer');
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        alert('支付失败: ' + error.message);
    });
}

async function shipOrder(orderId) {
    if (!confirm('确定发货？')) return;
    try {
        const response = await fetch(`${BASE_URL}/orders/${orderId}/ship`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('发货成功');
            loadOrders('seller');
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('发货失败: ' + error.message);
    }
}

async function completeOrder(orderId) {
    if (!confirm('确定完成交易？')) return;
    try {
        const response = await fetch(`${BASE_URL}/orders/${orderId}/complete`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('交易完成');
            loadOrders('seller');
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('操作失败: ' + error.message);
    }
}

async function cancelOrder(orderId) {
    if (!confirm('确定取消订单？')) return;
    try {
        const response = await fetch(`${BASE_URL}/orders/${orderId}/cancel`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('取消成功');
            loadProfile();
        } else if (data.code === 401) {
            alert('登录已过期，请重新登录');
            clearLoginState();
            showPage('login');
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('取消失败: ' + error.message);
    }
}

function openMessageModal() {
    if (!currentUserId) {
        const savedUserId = localStorage.getItem('userId');
        if (savedUserId) {
            currentUserId = parseInt(savedUserId);
        } else {
            alert('请先登录');
            showPage('login');
            return;
        }
    }
    
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.style.display = 'flex';
    modal.innerHTML = `
        <div class="modal-content">
            <h3>发送留言</h3>
            <textarea id="message-content" placeholder="输入留言内容..."></textarea>
            <button onclick="sendMessage()">发送</button>
            <button onclick="this.parentElement.parentElement.remove()">取消</button>
        </div>
    `;
    document.body.appendChild(modal);
}

async function sendMessage() {
    if (!currentProduct) return;
    if (!currentUserId) {
        alert('请先登录');
        return;
    }
    
    const content = document.getElementById('message-content').value;
    if (!content.trim()) {
        alert('请输入留言内容');
        return;
    }
    
    try {
        const response = await fetch(`${BASE_URL}/messages`, {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                productId: currentProduct.id,
                receiverId: currentProduct.sellerId,
                content
            })
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('留言发送成功');
            document.querySelector('.modal').remove();
        } else if (data.code === 401) {
            alert('登录已过期，请重新登录');
            clearLoginState();
            showPage('login');
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('发送失败: ' + error.message);
    }
}

function reviewOrder(orderId) {
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.style.display = 'flex';
    modal.innerHTML = `
        <div class="modal-content">
            <h3>评价商品</h3>
            <div class="rating-stars" onclick="setRating(event)">
                <span data-rating="1">★</span>
                <span data-rating="2">★</span>
                <span data-rating="3">★</span>
                <span data-rating="4">★</span>
                <span data-rating="5">★</span>
            </div>
            <textarea id="review-content" placeholder="输入评价内容..."></textarea>
            <button onclick="submitReview('${orderId}')">提交评价</button>
            <button onclick="this.parentElement.parentElement.remove()">取消</button>
        </div>
    `;
    document.body.appendChild(modal);
}

function setRating(e) {
    const star = e.target;
    if (!star.dataset.rating) return;
    
    const rating = parseInt(star.dataset.rating);
    document.querySelectorAll('.rating-stars span').forEach((s, i) => {
        s.style.color = i < rating ? '#ffc107' : '#ddd';
        s.dataset.currentRating = rating;
    });
}

async function submitReview(orderId) {
    const content = document.getElementById('review-content').value;
    const ratingElement = document.querySelector('.rating-stars span[data-current-rating]');
    const rating = ratingElement ? parseInt(ratingElement.dataset.currentRating) : 5;
    
    try {
        const response = await fetch(`${BASE_URL}/reviews`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ orderId, rating, content })
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('评价成功');
            document.querySelector('.modal').remove();
            loadProfile();
        } else {
            alert(data.message);
        }
    } catch (error) {
        alert('评价失败: ' + error.message);
    }
}

function openChangePasswordModal() {
    document.getElementById('change-password-modal').style.display = 'flex';
    document.getElementById('change-password-error').style.display = 'none';
    document.getElementById('old-password').value = '';
    document.getElementById('new-password').value = '';
    document.getElementById('confirm-password').value = '';
}

function closeChangePasswordModal() {
    document.getElementById('change-password-modal').style.display = 'none';
}

async function handleChangePassword(e) {
    e.preventDefault();
    
    const oldPassword = document.getElementById('old-password').value;
    const newPassword = document.getElementById('new-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    
    if (newPassword !== confirmPassword) {
        document.getElementById('change-password-error').textContent = '两次输入的密码不一致';
        document.getElementById('change-password-error').style.display = 'block';
        return;
    }
    
    if (newPassword.length < 6 || newPassword.length > 20) {
        document.getElementById('change-password-error').textContent = '密码长度必须在6-20位之间';
        document.getElementById('change-password-error').style.display = 'block';
        return;
    }
    
    try {
        const response = await fetch(`${BASE_URL}/users/change-password`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify({
                oldPassword,
                newPassword
            })
        });
        const data = await response.json();
        if (data.code === 200) {
            alert('密码修改成功，请重新登录');
            closeChangePasswordModal();
            clearLoginState();
            showPage('login');
        } else if (data.code === 401) {
            alert('登录已过期，请重新登录');
            closeChangePasswordModal();
            clearLoginState();
            showPage('login');
        } else {
            document.getElementById('change-password-error').textContent = data.message;
            document.getElementById('change-password-error').style.display = 'block';
        }
    } catch (error) {
        document.getElementById('change-password-error').textContent = '修改失败: ' + error.message;
        document.getElementById('change-password-error').style.display = 'block';
    }
}

function handleImageSelect(e) {
    const files = Array.from(e.target.files);
    
    files.forEach(file => {
        if (file.size > 10 * 1024 * 1024) {
            alert('图片大小不能超过10MB');
            return;
        }
        
        const reader = new FileReader();
        reader.onload = function(event) {
            const preview = document.createElement('div');
            preview.className = 'preview-item';
            preview.innerHTML = `
                <img src="${event.target.result}" alt="预览">
                <button class="remove-btn" onclick="removeImage(this)">×</button>
            `;
            document.getElementById('upload-preview').appendChild(preview);
            selectedImages.push(file);
        };
        reader.readAsDataURL(file);
    });
    
    e.target.value = '';
}

function removeImage(btn) {
    const previewItem = btn.parentElement;
    const index = Array.from(document.querySelectorAll('.preview-item')).indexOf(previewItem);
    
    if (index > -1) {
        selectedImages.splice(index, 1);
        previewItem.remove();
    }
}
