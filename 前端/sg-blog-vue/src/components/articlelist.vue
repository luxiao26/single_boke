<!-- 文章列表 -->
<template>
    <div> <!-- 添加一个根元素来包裹所有内容 -->
    <el-row class="searchBox article-search" style="position: fixed;top: 0.2%;right: -3%;width: 36%;z-index: 999; margin-right: 360px;">
        <el-col :span="8" :offset="8" style="margin-left: 0px">
<el-input ref="textInput" v-model="queryParams.search" placeholder="请输入文章标题" :maxlength="10"></el-input>

            <!-- <el-input id='textInput' v-model="queryParams.search" placeholder="请输入文章标题" :maxlength="10"></el-input> -->
        </el-col>
        <el-col :span="8" style="padding-right: 100px">
            <el-button type="primary" @click="handleSearch()">搜索</el-button>
        </el-col>
    </el-row>
    <el-row class="sharelistBox">

        <el-col :span="24" class="s-item tcommonBox" v-for="(item,index) in articleList" :key="'article'+index">
            <span class="s-round-date">
                <span class="month" v-html="showInitDate(item.createTime,'month')+'月'"></span>
                <span class="day" v-html="showInitDate(item.createTime,'date')"></span>
            </span>
            <header>
                <h1>
                    <a :href="'#/DetailArticle?aid='+item.id" target="_blank">
                        {{item.title}}
                    </a>
                </h1>
                <h2>
                    <i class="fa fa-fw fa-user"></i>发表于
                    <i class="fa fa-fw fa-clock-o"></i><span v-html="showInitDate(item.createTime,'all')">{{showInitDate(item.createTime,'all')}}</span> •
                    <i class="fa fa-fw fa-eye"></i>{{item.viewCount}} 次围观 •

                </h2>
                <div class="ui label">
                    <a :href="'#/Share?classId='+item.categoryId">{{item.categoryName}}</a>
                </div>

                <template v-if="item.tagNameList && item.tagNameList.length > 0">
                    <div class="tag-container">
                        <el-tag v-for="(tag, tagIndex) in item.tagNameList" :key="'tag'+tagIndex" :type="getTagType(tagIndex)" style="position: relative;top: -22px;margin-left: 8px;cursor: pointer;">
                            {{ tag }}
                        </el-tag>
                    </div>
                </template>

            </header>
            <div class="article-content">
                <p style="text-indent:2em;">
                    {{item.summary}}
                </p>
                <p  style="max-height:300px;overflow:hidden;text-align:center;">
                    <img :src="item.thumbnail" alt="" class="maxW">
                </p>
            </div>
            <div class="viewdetail">
                <a class="tcolors-bg" :href="'#/DetailArticle?aid='+item.id" target="_blank">
                    阅读全文>>
                </a>
            </div>

        </el-col>
         <el-col class="viewmore">
            <a v-show="hasMore" class="tcolors-bg" href="javascript:void(0);" @click="addMoreFun">点击加载更多</a>
            <a v-show="!hasMore" class="tcolors-bg" href="javascript:void(0);">暂无更多数据</a>
        </el-col>
    </el-row>
    </div>
</template>

<script>
import {initDate} from '../utils/server.js'
import {articleList} from '../api/article'
    export default {
        name:'Share',
        data() { //选项 / 数据
            return {
                // 查询参数
                queryParams: {
                    pageNum: 1,
                    pageSize: 10,
                    categoryId: 0,
                    search: '', // 初始化搜索内容为空字符串
                },
                articleList:[],
                hasMore:true,
                isLoadInput: false,
            }
        },

        methods: {
            onKeyUp(event) {
            if (event.key === 'Enter') {
                this.handleSearch();
            }
            },
            compositionEnd() {
                const value = this.queryParams.search;
                if (/[^\x00-\xff]/.test(value) || !value.trim()) {
                    this.handleSearch()
                }
                if (/^[a-zA-Z]+$/.test(value)) {
                    this.handleSearch()
                }

            },
            getTagType(index) {
                const types = ['success', 'info', 'warning', 'primary', 'danger'];
                return types[index % types.length];
            },
            showInitDate: function(oldDate,full){
                return initDate(oldDate,full)
            },
            handleSearch() {
                this.articleList = [];
                this.getList();
            },

            getList(){
                articleList(this.queryParams).then((response)=>{
                    this.articleList = this.articleList.concat(response.rows)
                    if(response.total<=this.articleList.length){
                        this.hasMore=false
                    }else{
                        this.hasMore=true
                        this.queryParams.pageNum++
                    }
                })
            },
            showSearchShowList:function(initData){//展示数据
                if(initData){
                    this.articleList = []

                }
                this.getList()
            },
            addMoreFun:function(){//查看更多
                this.showSearchShowList(false);
            },
            routeChange:function(){
                var that = this;
                this.queryParams.categoryId = (that.$route.query.classId==undefined?0:parseInt(that.$route.query.classId));//获取传参的classId
                this.showSearchShowList(true);
            }
        },
        components: { //定义组件

        },
        watch: {
           // 如果路由有变化，会再次执行该方法
           '$route':'routeChange',
           '$store.state.keywords':'routeChange'
         },
        created() { //生命周期函数
            // console.log(this.$route);
            var that = this;
            that.routeChange();
        },
        mounted() {
            this.$refs.textInput.$el.addEventListener('compositionend', this.compositionEnd);
            this.$refs.textInput.$el.addEventListener('keyup', this.onKeyUp);
        },
    }
</script>

<style>
@media (min-width: 1100px) {
  .article-search {
    position: fixed;
    top: 0.2%;
    right: 7%;
    width: 50%;
    z-index: 999;
  }
}

@media (max-width: 1100px) {
  .article-search {
    display: none;
  }
}
.tag-container {
    display: flex;
    margin-top: -37px;
    justify-content: center;
}
/*分享标题*/
.shareTitle{
    margin-bottom: 40px;
    position: relative;
    border-radius: 5px;
    background: #fff;
    padding:15px;
}
.shareclassTwo{
    width:100%;
}
.shareclassTwo li{
    display: inline-block;
}
.shareclassTwo li a{
    display: inline-block;
    padding:3px 7px;
    margin:5px 10px;
    color:#fff;
    border-radius: 4px;
    background: #64609E;
    border: 1px solid #64609E;
    transition: transform 0.2s linear;
    -webkit-transition: transform 0.2s linear;
}
.shareclassTwo li a:hover{
    transform: translate(0,-3px);
    -webkit-transform: translate(0,-3px);
}
.shareclassTwo li a.active{
    background: #fff;
    color:#64609E;

}
/*文章列表*/
    .sharelistBox{
        transition: all 0.5s ease-out;
        font-size: 15px;
    }


    /*.sharelistBox .viewmore a:hover,.s-item .viewdetail a:hover{
        background: #48456C;
    }*/
</style>
