<template>
  <view class="container">
    <view class="header">
      <view class="avatar">{{ nickname.charAt(0) }}</view>
      <text class="nickname">{{ nickname }}</text>
    </view>
    <view v-if="!isLogin" class="login-btn" @tap="doLogin">微信登录</view>
    <view v-else class="menu-list">
      <view class="menu-item" @tap="goOrders">
        <text>我的订单</text>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="clearCart">
        <text>清空购物车</text>
        <text class="arrow">></text>
      </view>
      <view class="logout-btn" @tap="doLogout">退出登录</view>
    </view>
  </view>
</template>

<script>
import request from '../../utils/request'

export default {
  data() {
    return {
      isLogin: false,
      nickname: '未登录'
    }
  },
  onShow() {
    const token = uni.getStorageSync('token')
    const nick = uni.getStorageSync('nickname')
    this.isLogin = !!token
    this.nickname = nick || '未登录'
  },
  methods: {
    async doLogin() {
      // 模拟微信登录，实际需调用uni.login获取code
      try {
        const res = await request({
          url: '/login',
          method: 'POST',
          data: { code: 'mock_code_' + Date.now() }
        })
        uni.setStorageSync('token', res.data.token)
        uni.setStorageSync('nickname', res.data.nickname)
        this.isLogin = true
        this.nickname = res.data.nickname
        uni.showToast({ title: '登录成功', icon: 'success' })
      } catch (e) {
        // handled
      }
    },
    goOrders() {
      uni.switchTab({ url: '/pages/order/order' })
    },
    clearCart() {
      uni.removeStorageSync('cart')
      uni.showToast({ title: '购物车已清空', icon: 'success' })
    },
    doLogout() {
      uni.removeStorageSync('token')
      uni.removeStorageSync('nickname')
      this.isLogin = false
      this.nickname = '未登录'
      uni.showToast({ title: '已退出', icon: 'success' })
    }
  }
}
</script>

<style scoped>
.container { padding: 32rpx; }
.header { display: flex; align-items: center; background: #fff; border-radius: 16rpx; padding: 40rpx; margin-bottom: 32rpx; }
.avatar { width: 100rpx; height: 100rpx; border-radius: 50%; background: #e74c3c; color: #fff; font-size: 48rpx; display: flex; align-items: center; justify-content: center; }
.nickname { margin-left: 24rpx; font-size: 34rpx; font-weight: bold; }
.login-btn { background: #e74c3c; color: #fff; text-align: center; padding: 24rpx; border-radius: 40rpx; font-size: 30rpx; }
.menu-list { background: #fff; border-radius: 16rpx; overflow: hidden; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 32rpx; border-bottom: 1rpx solid #f5f5f5; font-size: 28rpx; }
.arrow { color: #999; }
.logout-btn { text-align: center; padding: 32rpx; color: #e74c3c; font-size: 28rpx; }
</style>
