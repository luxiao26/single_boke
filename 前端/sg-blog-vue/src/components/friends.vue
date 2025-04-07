<!-- 友情链接模块 -->
<template>
<div class="tFriendsBox">
    <h1>欢迎申请友链，格式: </h1>
    <el-row>
      <el-button type="info" @click="handleAdd" style="float: right;margin-top: -50px;z-index:999;">申请友链</el-button>
    </el-row>
    <h3>        网站名称: HF博客</h3>
    <h3>网站地址: https://www.baidu.com </h3>
    <h3>网站描述: 百度一下全都知道</h3>
    <h3>网站logo: https://xxxxx.xx/logo.jpg</h3>


    <el-row>
        <el-col :span="12" class="tf-item" v-for="(item,index) in friendslink" :key="'f'+index">
            <a :href="item.address" target="_blank">
                <img :src="item.logo?item.logo:'static/img/tou.jpg'"  :onerror="$store.state.errorImg">
                <h4>{{item.name}}</h4>
                <p>{{item.description}}</p>
            </a>
        </el-col>
    </el-row>

    <!-- 添加或修改友链对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="logo" prop="logo">
          <el-input v-model="form.logo" placeholder="请输入logo地址" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">提 交</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
</div>
</template>

<script>
import { getAllLink, addLink } from "../api/link.js";
export default {
  data() {
    //选项 / 数据
    return {
      friendslink: [], //友情链接
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 友链表格数据
      linkList: null,
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        description: null,
        address: null,
        logo: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
    };
  },
  methods: {
    //事件处理器
    getList() {
      getAllLink().then((response) => {
        this.friendslink = response;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        name: null,
        description: null,
        address: null,
        logo: null,
        status: '2'
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {

      if (!localStorage.getItem('userInfo')) {
        return this.$message.warning('请先登录，才能申请友链')
      }
      this.reset()
      this.open = true
      this.title = '申请友链'

      this.$notify({
          title: '友链提示',
          message: '你的友链申请提交后，如3天内未得到通过，则默认表示被拒'
        });
    },
    resetForm(refName) {
      if (this.$refs[refName]) {
        this.$refs[refName].resetFields()
      }
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate((valid) => {
        addLink(this.form).then((response) => {
              this.$message.success( '提交成功');
              this.open = false
              this.getList()
            })
      })
    },
  },
  components: {
    //定义组件
  },
  created() {
    //生命周期函数
    this.getList();
  },
};
</script>

<style>
.tFriendsBox {
  background: #fff;
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 40px;
}
.tFriendsBox h1 {
  padding: 15px 0;
  font-size: 25px;
  font-weight: bold;
}

.tFriendsBox h3 {
  padding: 8px 0;
  font-size: 20px;
  color: #c0cdd7;
}
.tFriendsBox .tf-item {
  transition: all 0.3s ease-out;
  border-radius: 5px;
  position: relative;
}
.tFriendsBox .tf-item:hover {
  background: rgba(230, 244, 250, 0.5);
}
.tFriendsBox .tf-item a {
  display: block;
  padding: 0 10px 0 90px;
  height: 90px;
}
.tFriendsBox .tf-item a img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  position: absolute;
  top: 15px;
  left: 15px;
  cursor: pointer;
  object-fit: cover;
}
.tFriendsBox .tf-item a h4 {
  cursor: pointer;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  font-size: 20px;
  padding-top: 15px;
  font-weight: bold;
}
.tFriendsBox .tf-item a p {
  margin: 10px 0;
  font-size: 12px;
  line-height: 24px;
  color: #999;
  cursor: pointer;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
