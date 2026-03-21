<template>
  <view class="container" v-if="detail">
    <view class="card">
      <view class="row"><text class="label">订单号</text><text>{{ detail.order.orderNo }}</text></view>
      <view class="row"><text class="label">桌号</text><text>{{ detail.order.tableNo }}</text></view>
      <view class="row"><text class="label">状态</text><text :class="'status-' + detail.order.status">{{ statusText(detail.order.status) }}</text></view>
      <view class="row"><text class="label">总金额</text><text class="price">¥{{ (detail.order.totalAmount / 100).toFixed(2) }}</text></view>
      <view class="row"><text class="label">备注</text><text>{{ detail.order.remark || '无' }}</text></view>
      <view class="row"><text class="label">下单时间</text><text>{{ detail.order.createTime }}</text></view>
    </view>
    <view class="card">
      <view class="card-title">菜品明细</view>
      <view v-for="item in detail.items" :key="item.id" class="item-row">
        <text class="item-name">{{ item.dishName }}</text>
        <text class="item-qty">x{{ item.quantity }}</text>
        <text class="item-price">¥{{ (item.dishPrice * item.quantity / 100).toFixed(2) }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import request from '../../utils/request'

export default {
  data() {
    return { detail: null }
  },
  onLoad(options) {
    this.loadDetail(options.id)
  },
  methods: {
    statusText(s) {
      return ['待支付', '已支付', '制作中', '已完成', '已取消'][s] || '未知'
    },
    async loadDetail(id) {
      const res = await request({ url: '/order/detail/' + id })
      this.detail = res.data
    }
  }
}
</script>

<style scoped>
.container { padding: 16rpx; }
.card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.card-title { font-size: 30rpx; font-weight: bold; margin-bottom: 16rpx; }
.row { display: flex; justify-content: space-between; padding: 12rpx 0; font-size: 28rpx; border-bottom: 1rpx solid #f5f5f5; }
.label { color: #999; }
.price { color: #e74c3c; font-weight: bold; }
.status-0 { color: #f39c12; }
.status-1 { color: #3498db; }
.status-2 { color: #e67e22; }
.status-3 { color: #27ae60; }
.status-4 { color: #999; }
.item-row { display: flex; justify-content: space-between; padding: 12rpx 0; font-size: 26rpx; border-bottom: 1rpx solid #f5f5f5; }
.item-name { flex: 1; }
.item-qty { width: 80rpx; text-align: center; color: #999; }
.item-price { width: 140rpx; text-align: right; color: #e74c3c; }
</style>
