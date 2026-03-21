<template>
  <view class="container">
    <view class="card">
      <view class="title">评价订单</view>
      <view class="rating-row">
        <text>评分：</text>
        <view class="stars">
          <view v-for="i in 5" :key="i" :class="['star', i <= rating ? 'active' : '']" @tap="rating = i">★</view>
        </view>
      </view>
      <textarea v-model="content" placeholder="请输入您的评价..." class="textarea" />
      <view class="submit-btn" @tap="submitReview">提交评价</view>
    </view>
  </view>
</template>

<script>
import request from '../../utils/request'

export default {
  data() {
    return {
      orderId: null,
      rating: 5,
      content: ''
    }
  },
  onLoad(options) {
    this.orderId = options.orderId
  },
  methods: {
    async submitReview() {
      if (!this.content.trim()) {
        uni.showToast({ title: '请输入评价内容', icon: 'none' })
        return
      }
      try {
        await request({
          url: '/review/submit',
          method: 'POST',
          data: {
            orderId: this.orderId,
            rating: this.rating,
            content: this.content
          }
        })
        uni.showToast({ title: '评价成功', icon: 'success' })
        setTimeout(() => { uni.navigateBack() }, 1000)
      } catch (e) {
        // handled
      }
    }
  }
}
</script>

<style scoped>
.container { padding: 16rpx; }
.card { background: #fff; border-radius: 16rpx; padding: 32rpx; }
.title { font-size: 34rpx; font-weight: bold; margin-bottom: 32rpx; }
.rating-row { display: flex; align-items: center; margin-bottom: 24rpx; font-size: 28rpx; }
.stars { display: flex; margin-left: 16rpx; }
.star { font-size: 48rpx; color: #ddd; margin-right: 8rpx; }
.star.active { color: #f39c12; }
.textarea { width: 100%; height: 200rpx; border: 1rpx solid #eee; border-radius: 12rpx; padding: 16rpx; font-size: 28rpx; box-sizing: border-box; }
.submit-btn { margin-top: 32rpx; background: #e74c3c; color: #fff; text-align: center; padding: 24rpx; border-radius: 40rpx; font-size: 30rpx; }
</style>
