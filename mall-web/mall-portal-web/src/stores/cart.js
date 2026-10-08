import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const cartList = ref(JSON.parse(localStorage.getItem('cart_list') || '[]'))

  const cartCount = computed(() => cartList.value.reduce((sum, item) => sum + item.quantity, 0))

  const setCartList = (list) => {
    cartList.value = list
    localStorage.setItem('cart_list', JSON.stringify(list))
  }

  const addToCart = (item) => {
    const exist = cartList.value.find(c => c.skuId === item.skuId)
    if (exist) {
      exist.quantity += item.quantity
    } else {
      cartList.value.push(item)
    }
    setCartList([...cartList.value])
  }

  const removeFromCart = (id) => {
    cartList.value = cartList.value.filter(c => c.id !== id)
    setCartList(cartList.value)
  }

  const updateQuantity = (id, quantity) => {
    const item = cartList.value.find(c => c.id === id)
    if (item) {
      item.quantity = quantity
      setCartList([...cartList.value])
    }
  }

  const clearCart = () => {
    cartList.value = []
    localStorage.removeItem('cart_list')
  }

  return { cartList, cartCount, setCartList, addToCart, removeFromCart, updateQuantity, clearCart }
})
