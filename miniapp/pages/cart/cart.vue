<template>
  <view class="container">
    <view v-if="cart.length === 0" class="empty">
      <text>购物车为空</text>
    </view>
    <view v-else>
      <view v-for="(item, index) in cart" :key="item.dishId" class="cart-item">
        <image v-if="item.image" :src="formatImage(item.image)" class="item-img" mode="aspectFill" />
        <view v-else class="item-img placeholder">暂无图</view>
        <view class="item-info">
          <text class="item-name">{{ item.dishName }}</text>
          <text class="item-price">¥{{ (item.dishPrice / 100).toFixed(2) }}</text>
        </view>
        <view class="quantity-ctrl">
          <view class="qty-btn" @tap="changeQty(index, -1)">-</view>
          <text class="qty-num">{{ item.quantity }}</text>
          <view class="qty-btn" @tap="changeQty(index, 1)">+</view>
        </view>
      </view>

      <view class="table-input">
        <text>桌号：</text>
        <input v-model="tableNo" placeholder="请输入桌号" type="text" />
      </view>
      <view class="table-input">
        <text>备注：</text>
        <input v-model="remark" placeholder="如：不要辣、少盐等" type="text" />
      </view>

      <view class="bottom-bar">
        <view class="total">合计：<text class="total-price">¥{{ (totalAmount / 100).toFixed(2) }}</text></view>
        <view class="submit-btn" @tap="submitOrder">提交订单</view>
      </view>
    </view>
  </view>
</template>

<script>
import request from '../../utils/request'

const FILE_HOST = 'http://localhost:8080'

export default {
  data() {
    return {
      cart: [],
      tableNo: '',
      remark: ''
    }
  },
  computed: {
    totalAmount() {
      return this.cart.reduce((sum, item) => sum + item.dishPrice * item.quantity, 0)
    }
  },
  onShow() {
    this.cart = uni.getStorageSync('cart') || []
  },
  methods: {
    formatImage(path) {
      if (!path) return ''
      return path.startsWith('http') ? path : FILE_HOST + path
    },
    changeQty(index, delta) {
      this.cart[index].quantity += delta
      if (this.cart[index].quantity <= 0) {
        this.cart.splice(index, 1)
      }
      uni.setStorageSync('cart', this.cart)
    },
    async submitOrder() {
      if (!this.tableNo) {
        uni.showToast({ title: '请输入桌号', icon: 'none' })
        return
      }
      if (this.cart.length === 0) {
        uni.showToast({ title: '购物车为空', icon: 'none' })
        return
      }
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        uni.switchTab({ url: '/pages/mine/mine' })
        return
      }
      try {
        const res = await request({
          url: '/order/submit',
          method: 'POST',
          data: {
            tableNo: this.tableNo,
            remark: this.remark,
            items: this.cart.map(item => ({
              dishId: item.dishId,
              dishName: item.dishName,
              dishPrice: item.dishPrice,
              quantity: item.quantity
            }))
          }
        })
        uni.removeStorageSync('cart')
        uni.showToast({ title: '下单成功', icon: 'success' })
        setTimeout(() => {
          uni.switchTab({ url: '/pages/order/order' })
        }, 1000)
      } catch (e) {
        // handled in request
      }
    }
  }
}
</script>

<style scoped>
.container { padding: 16rpx; padding-bottom: 140rpx; }
.empty { text-align: center; padding: 200rpx 0; color: #999; font-size: 30rpx; }
.cart-item { display: flex; align-items: center; background: #fff; border-radius: 16rpx; padding: 20rpx; margin-bottom: 16rpx; }
.item-img { width: 120rpx; height: 120rpx; border-radius: 12rpx; flex-shrink: 0; }
.placeholder { background: #eee; display: flex; align-items: center; justify-content: center; font-size: 22rpx; color: #999; }
.item-info { flex: 1; margin-left: 20rpx; }
.item-name { font-size: 28rpx; font-weight: bold; display: block; }
.item-price { font-size: 26rpx; color: #e74c3c; }
.quantity-ctrl { display: flex; align-items: center; }
.qty-btn { width: 48rpx; height: 48rpx; border-radius: 50%; background: #eee; text-align: center; line-height: 48rpx; font-size: 32rpx; }
.qty-num { margin: 0 16rpx; font-size: 28rpx; }
.table-input { display: flex; align-items: center; background: #fff; border-radius: 16rpx; padding: 20rpx; margin-bottom: 16rpx; }
.table-input text { font-size: 28rpx; width: 100rpx; }
.table-input input { flex: 1; font-size: 28rpx; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; display: flex; align-items: center; justify-content: space-between; padding: 20rpx 32rpx; box-shadow: 0 -2rpx 10rpx rgba(0,0,0,.06); }
.total { font-size: 28rpx; }
.total-price { color: #e74c3c; font-size: 36rpx; font-weight: bold; }
.submit-btn { background: #e74c3c; color: #fff; padding: 16rpx 48rpx; border-radius: 40rpx; font-size: 28rpx; }
</style>
