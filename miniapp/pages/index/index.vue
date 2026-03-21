<template>
  <view class="container">
    <scroll-view scroll-x class="category-bar">
      <view
        v-for="c in categories"
        :key="c.id"
        :class="['cat-item', activeCategory === c.id ? 'active' : '']"
        @tap="selectCategory(c.id)"
      >
        {{ c.name }}
      </view>
    </scroll-view>

    <scroll-view scroll-y class="dish-list">
      <view v-for="dish in dishes" :key="dish.id" class="dish-item">
        <image
          v-if="dish.image"
          :src="formatImage(dish.image)"
          class="dish-img"
          mode="aspectFill"
        />
        <view v-else class="dish-img placeholder">暂无图</view>

        <view class="dish-info">
          <text class="dish-name">{{ dish.name }}</text>
          <text class="dish-desc">{{ dish.description || '' }}</text>
          <view class="dish-bottom">
            <text class="dish-price">¥{{ (dish.price / 100).toFixed(2) }}</text>
            <view class="cart-btn" @tap="addToCart(dish)">+</view>
          </view>
        </view>
      </view>

      <view v-if="!dishes.length" class="empty">暂无菜品</view>
    </scroll-view>
  </view>
</template>

<script>
import request from '../../utils/request'

const FILE_HOST = 'http://localhost:8080'

export default {
  data() {
    return {
      categories: [],
      dishes: [],
      activeCategory: null
    }
  },
  onShow() {
    this.loadCategories()
  },
  methods: {
    formatImage(path) {
      if (!path) return ''
      return path.startsWith('http') ? path : FILE_HOST + path
    },
    async loadCategories() {
      const res = await request({ url: '/category/list' })
      this.categories = res.data || []
      if (this.categories.length > 0 && !this.activeCategory) {
        this.activeCategory = this.categories[0].id
      }
      this.loadDishes()
    },
    async loadDishes() {
      const url = this.activeCategory ? `/dish/list?categoryId=${this.activeCategory}` : '/dish/list'
      const res = await request({ url })
      this.dishes = res.data || []
    },
    selectCategory(id) {
      this.activeCategory = id
      this.loadDishes()
    },
    addToCart(dish) {
      const cart = uni.getStorageSync('cart') || []
      const idx = cart.findIndex((item) => item.dishId === dish.id)
      if (idx > -1) {
        cart[idx].quantity++
      } else {
        cart.push({
          dishId: dish.id,
          dishName: dish.name,
          dishPrice: dish.price,
          image: dish.image,
          quantity: 1
        })
      }
      uni.setStorageSync('cart', cart)
      uni.showToast({ title: '已加入购物车', icon: 'success' })
    }
  }
}
</script>

<style scoped>
.container { display: flex; flex-direction: column; height: 100vh; }
.category-bar { white-space: nowrap; background: #fff; padding: 16rpx 10rpx; border-bottom: 1rpx solid #eee; }
.cat-item { display: inline-block; margin: 0 8rpx; padding: 10rpx 24rpx; border-radius: 30rpx; font-size: 26rpx; background: #f3f3f3; color: #666; }
.cat-item.active { background: #e74c3c; color: #fff; }
.dish-list { flex: 1; padding: 16rpx; }
.dish-item { display: flex; background: #fff; border-radius: 12rpx; padding: 16rpx; margin-bottom: 16rpx; }
.dish-img { width: 160rpx; height: 160rpx; border-radius: 10rpx; flex-shrink: 0; }
.placeholder { background: #eee; display: flex; align-items: center; justify-content: center; color: #999; font-size: 22rpx; }
.dish-info { flex: 1; margin-left: 16rpx; display: flex; flex-direction: column; justify-content: space-between; }
.dish-name { font-size: 30rpx; font-weight: bold; }
.dish-desc { font-size: 24rpx; color: #999; }
.dish-bottom { display: flex; justify-content: space-between; align-items: center; }
.dish-price { color: #e74c3c; font-size: 30rpx; font-weight: bold; }
.cart-btn { width: 48rpx; height: 48rpx; line-height: 48rpx; border-radius: 50%; text-align: center; color: #fff; background: #e74c3c; font-size: 34rpx; }
.empty { text-align: center; color: #999; padding: 120rpx 0; }
</style>
