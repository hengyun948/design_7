<template>
  <view class="container">
    <view class="tabs">
      <view v-for="(t, i) in tabs" :key="i" :class="['tab', activeTab === i ? 'active' : '']" @tap="switchTab(i)">
        {{ t }}
      </view>
    </view>
    <scroll-view scroll-y class="order-list">
      <view v-for="order in orders" :key="order.id" class="order-card" @tap="goDetail(order.id)">
        <view class="order-header">
          <text class="order-no">{{ order.orderNo }}</text>
          <text :class="['status', 'status-' + order.status]">{{ statusText(order.status) }}</text>
        </view>
        <view class="order-info">
          <text>桌号：{{ order.tableNo }}</text>
          <text class="order-amount">¥{{ (order.totalAmount / 100).toFixed(2) }}</text>
        </view>
        <view v-if="order.items && order.items.length" class="order-items">
          <text v-for="item in order.items" :key="item.id" class="order-item-line">
            {{ item.dishName }} x{{ item.quantity }}
          </text>
        </view>
        <view class="order-time">{{ order.createTime }}</view>
        <view class="order-actions" v-if="order.status === 0">
          <view class="pay-btn" @tap.stop="payOrder(order.id)">去支付</view>
        </view>
        <view class="order-actions" v-if="order.status === 3">
          <view class="review-btn" @tap.stop="goReview(order.id)">去评价</view>
        </view>
      </view>
      <view v-if="orders.length === 0" class="empty">暂无订单</view>
    </scroll-view>
  </view>
</template>

<script>
import request from '../../utils/request'

export default {
  data() {
    return {
      tabs: ['全部', '待支付', '已支付', '制作中', '已完成'],
      activeTab: 0,
      orders: []
    }
  },
  onShow() {
    this.loadOrders()
  },
  methods: {
    statusText(s) {
      return ['待支付', '已支付', '制作中', '已完成', '已取消'][s] || '未知'
    },
    switchTab(i) {
      this.activeTab = i
      this.loadOrders()
    },
    async loadOrders() {
      const params = { page: 1, size: 50 }
      if (this.activeTab > 0) {
        params.status = this.activeTab - 1
      }
      const query = Object.keys(params).map(k => k + '=' + params[k]).join('&')
      const res = await request({ url: '/order/list?' + query })
      const records = res.data.records || []
      const details = await Promise.all(records.map(async (order) => {
        try {
          const detailRes = await request({ url: '/order/detail/' + order.id })
          return { ...order, items: detailRes.data.items || [] }
        } catch (e) {
          return { ...order, items: [] }
        }
      }))
      this.orders = details
    },
    goDetail(id) {
      uni.navigateTo({ url: '/pages/order-detail/order-detail?id=' + id })
    },
    async payOrder(id) {
      await request({ url: '/order/pay', method: 'POST', data: { orderId: id } })
      uni.showToast({ title: '支付成功', icon: 'success' })
      this.loadOrders()
    },
    goReview(orderId) {
      uni.navigateTo({ url: '/pages/review/review?orderId=' + orderId })
    }
  }
}
</script>

<style scoped>
.container { display: flex; flex-direction: column; height: 100vh; }
.tabs { display: flex; background: #fff; border-bottom: 1rpx solid #eee; }
.tab { flex: 1; text-align: center; padding: 20rpx 0; font-size: 26rpx; color: #666; }
.tab.active { color: #e74c3c; border-bottom: 4rpx solid #e74c3c; }
.order-list { flex: 1; padding: 16rpx; }
.order-card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.order-header { display: flex; justify-content: space-between; margin-bottom: 12rpx; }
.order-no { font-size: 24rpx; color: #999; }
.status { font-size: 24rpx; }
.status-0 { color: #f39c12; }
.status-1 { color: #3498db; }
.status-2 { color: #e67e22; }
.status-3 { color: #27ae60; }
.status-4 { color: #999; }
.order-info { display: flex; justify-content: space-between; font-size: 28rpx; margin-bottom: 8rpx; }
.order-amount { color: #e74c3c; font-weight: bold; }
.order-items { margin-bottom: 8rpx; }
.order-item-line { display: block; font-size: 24rpx; color: #666; margin-bottom: 4rpx; }
.order-time { font-size: 24rpx; color: #999; }
.order-actions { display: flex; justify-content: flex-end; margin-top: 16rpx; }
.pay-btn, .review-btn { padding: 10rpx 32rpx; border-radius: 32rpx; font-size: 24rpx; color: #fff; }
.pay-btn { background: #e74c3c; }
.review-btn { background: #27ae60; }
.empty { text-align: center; padding: 200rpx 0; color: #999; }
</style>
