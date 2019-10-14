<template>
  <div id="app">
    <index></index>
  </div>
</template>

<script>
import Index from '@/components/index'
export default {
  name: 'app',
  components: {
    'index': Index
  }
}
</script>

<style>
  *{
    padding: 0;
    margin: 0;
    border: 0;
    text-decoration: none;
    list-style-type: none;
  }
  #app {
    font-family: "Helvetica Neue",Helvetica,"Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
    width: 100%;
  }
  .center-box{
    position: relative;
    left: 50%;
    transform: translate(-50%,0);
  }
</style>
