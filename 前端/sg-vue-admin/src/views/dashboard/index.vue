<template>
  <div class="dashboard-container">
    <div class="dashboard-text">
      博客管理系统
    </div>
    <h1 style="margin-left: 36%; margin-top: 16%;">是哪位java卷王让我看看</h1>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'Dashboard',
  computed: {
    ...mapGetters([
      'name'
    ])
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
</style>
