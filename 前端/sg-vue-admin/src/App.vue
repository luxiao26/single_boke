<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  metaInfo() {
    return {
      title: this.$store.state.settings.dynamicTitle && this.$store.state.settings.title,
      titleTemplate: title => {
        return title ? `${title} - ${process.env.VUE_APP_TITLE}` : process.env.VUE_APP_TITLE
      }
    }
  }
}

</script>

<style>
    .footer {
      margin-top: 5px;
      height: 1px;
      width: 100%;
      text-align: center;
      opacity: 0.8;
      display: block;
      font-family: Arial, "STHeiti", Helvetica, sans-serif;
      font-size: 12px;
      line-height: 9px;
      color: black;
    
    } 

    a {
      text-decoration: none;
    }
  </style>
