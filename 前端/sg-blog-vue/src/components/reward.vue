<!-- 赞赏模块 -->
<template>
  <div class="tRewardBox tcommonBox">
    <header>
      
    </header>
    <section>
      <h1>赞赏方式：</h1>
      <el-row :gutter="30">
        <el-col :span="12" class="donate-item">
          <div class="donate-tip" @click="openImagePreview('static/img/wx_pay.png')">
            <img src="static/img/wx_pay.png"/>
            <span>你的赞赏是不断更新的动力</span>
          </div>
        </el-col>
      </el-row>
      <h1>@赞赏记录：</h1>
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column
          prop="payTime"
          label="日期"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="name"
          label="用户"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="money"
          label="金额"
          align="center"
        ></el-table-column>
      </el-table>
    </section>
    <!-- 图片预览 Dialog -->
    <el-dialog :visible.sync="imagePreviewVisible" width="30%" title="赞赏">
      <img :src="previewImageUrl" style="width: 100%;" />
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    //选项 / 数据
    return {
      rewardData: "", //赞赏二维码
      tableData: [
        {
          //赞赏数据
          "payTime":"2023-10-23",
          "name":"测试01",
          "money":"10"
        },
        {
          "payTime":"2023-10-28",
          "name":"测试02",
          "money":"10"
        },
        {
          "payTime":"2023-11-03",
          "name":"测试03",
          "money":"10"
        },
        {
          "payTime":"2025-05-16",
          "name":"测试04",
          "money":"10"
        },
        {
          "payTime":"2025-05-16",
          "name":"测试05",
          "money":"10"
        },
      ],
      imagePreviewVisible: false,
      previewImageUrl: '',
    };
  },
  methods: {
    //事件处理器
    showInitDate: function (date, full) {
      //时间处理
      return initDate(date, full);
    },
    openImagePreview(url) {
      this.previewImageUrl = url;
      this.imagePreviewVisible = true;
    }
  },
  components: {
    //定义组件
  },
  created() {
    //生命周期函数
    var that = this;
  },
};
</script>

<style>
.el-dialog--small {
    top: 20% !important;
    width: 25% !important;
}
.tRewardBox section {
  padding-bottom: 20px;
}
.tRewardBox section h1 {
  margin: 10px 0;
  font-size: 25px;
  font-weight: 700;
  /*text-align: center;*/
  line-height: 30px;
}
.tRewardBox section p {
  margin: 10px 0;
  line-height: 24px;
}
.tRewardBox section hr {
  background: #ccc;
  margin-bottom: 30px;
}

.tRewardBox .donate-item {
  text-align: right;
  color: #44b549;
}
.tRewardBox .donate-item:last-child {
  text-align: left;
  color: #00a0e9;
}
.tRewardBox .donate-item img {
  width: 100%;
  display: block;
  height: auto;
}
.tRewardBox .donate-item div {
  display: inline-block;
  width: 150px;
  padding: 0 6px;
  text-align: center;
  box-sizing: border-box;
}
.tRewardBox .donate-item div span {
  display: inline-block;
  width: 100%;
  margin: 10px 0;
  text-align: center;
}
.tRewardBox .el-table__body-wrapper {
  overflow: hidden;
}
.el-table--enable-row-hover .el-table__body tr:hover > td {
  background: transparent;
}
</style>
