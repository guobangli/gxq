<!-- 数据库资源面板 -->
<template>
  <Layout>
    <Content>
      <Breadcrumb>
        <BreadcrumbItem>数据库资源面板</BreadcrumbItem>
      </Breadcrumb>
      <Card>
        <div class="card-container">
          <div class="card-list">
            <Card style="height:160px;background: #fff;color:#495060;">
              <div class="card-content" style="width:100%;">
                <p style="line-height: 42px;"><span style="color:#0099CC">数据库：</span>{{databaseNum}}</p>
              </div>
              <div class="card-content">
                <p style="line-height: 42px;"><span>本月新增：</span>{{addNum}}</p>
              </div>
              <div class="card-content">
                <p style="line-height: 42px;"><span>新增占比：</span>{{newProportion}}%</p>
              </div>
              <div class="card-content">
                <p style="line-height: 42px;"><span>新增环比：</span>{{newRingRatio}}%</p>
              </div>
              <div class="card-content">
                <p style="line-height: 42px;"><span>净增：</span>{{netIncrease}}</p>
              </div> 
              <div class="card-content">
                <p style="line-height: 42px;"><span>本月释放：</span>{{release}}</p>
              </div>
              <div class="card-content">
                <p style="line-height: 42px;"><span>释放占比：</span>{{releaseRatio}}%</p>
              </div>
              <div class="card-content">
                <p style="line-height: 42px;"><span>释放环比：</span>{{releaseRingRatio}}%</p>
              </div>
              <div class="card-content">
                <p style="line-height: 42px;"><span>净增占比：</span>{{netIncreaseRatio}}%</p>
              </div>
            </Card>
          </div>
        </div>
        <Row type="flex" justify="space-between">
          <Col style="width: 49%">
            <chart-card title="数据库数量统计图">
              <lineChart ref="databaseCount">
                <div id="databaseCount" style="height: 400px;"></div>
              </lineChart>
            </chart-card>
          </Col>
          <Col style="width: 49%">
            <chart-card title="数据库类型占比统计图">
              <chartLine ref="databaseType" el="databaseType" :lineOption="databaseType">
                <div id="databaseType" style="height: 400px;"></div>
              </chartLine>
            </chart-card>
          </Col>
        </Row>
      </Card>
    </Content>
  </Layout>
</template>

<script>
// echart图外层容器，包括卡片样式
import chartCard from '@/view/home/chartCard.vue'
import lineChart from '@/view/home/lineChart.vue'
import chartLine from './common/ChartLine.vue'
import api from '@/api/axiosApi'
import softhardApiList from '@/api/softhardApiList'
import {dataPie} from '@/assets/js/echartOption'
export default {
  components:{
    chartCard,
    lineChart,
    chartLine
  },
  data() {
    return {
      databaseNum:0,//数据库数量
      addNum: 0,//新增
      release: 0,//释放
      newProportion: 0,//新增占比
      releaseRatio:0,//释放占比
      newRingRatio: 0,//新增环比
      releaseRingRatio:0,//释放环比
      netIncrease: 0,//净增
      netIncreaseRatio: 0,//净增占比
      databaseType: dataPie(),
    }
  },
  mounted () {
    this.init();
    // this.getDatabaseType()
  },
  methods: {
    init(){//初始化
      this.databaseResPanel();
      this.databaseStatistics();
      this.databaseTypeRatioStatistics();
    },
    databaseResPanel() {//获取数据库资源面板数据
      api(softhardApiList.databaseResPanel).then((res) => {
        if(res.status == 200 && res.data.data) {
          let data = res.data.data;
          this.databaseNum = data.databaseNum;
          this.addNum = data.addNum;
          this.release = data.release;
          this.newProportion = data.newProportion.toFixed(2);
          this.releaseRatio = data.releaseRatio.toFixed(2);
          this.newRingRatio  = data.newRingRatio.toFixed(2);
          this.releaseRingRatio  = data.releaseRingRatio.toFixed(2);
          this.netIncrease  = data.netIncrease;
          this.netIncreaseRatio  = data.netIncreaseRatio.toFixed(2);
        }
      }, (err) => {})
    },
    databaseStatistics() {//数据库数量统计
      let mon =[],arr=[];
      api(softhardApiList.databaseStatistics).then((res) => {
        if(res.status == 200 && res.data.data) {
          for(var i in res.data.data){
            mon.push(i);
            arr.push(res.data.data[i]);
          }
          this.drawServiceAdd(mon,arr)
        }
      }, (err) => {})
    },
    databaseTypeRatioStatistics() {//数据库类型占比统计
      let legendName =[],dataVal=[];
      api(softhardApiList.databaseTypeRatioStatistics).then((res) => {
        if(res.status == 200 && res.data.data) {
          for(var i in res.data.data){
            legendName.push(i);
            dataVal.push({value:res.data.data[i],name:i});
          }
          this.getDatabaseType(legendName,dataVal)
        }
      }, (err) => {})
    },
    // 绘制查询当前月及前十一个月的服务器数量分析图表
    drawServiceAdd (mon,arr) {
      const vm = this
      const opts = {
        el: 'databaseCount',
        title: {
          text: '近一年数据库新增数'
        },
        xAxis: {
          name: '月份',
          data:  mon
        },
        yAxis: {
          name: '数目'
        },
        series: [{
          name: '数据库新增数',
          type:'line',
          data: arr
        }]
      }
      vm.$refs.databaseCount.refresh(opts)
    },
    getDatabaseType (legendName,dataVal) {
      // dataVal = [
      //     { value: 335, name: '直接访问' },
      //     { value: 310, name: '邮件营销' },
      //     { value: 234, name: '联盟广告' },
      //     { value: 135, name: '视频广告' },
      //     { value: 1548, name: '搜索引擎' }
      //   ];
      this.databaseType.legend.data = legendName;
      this.databaseType.series[0].data = dataVal;
      this.databaseType.title.text = '数据库类型占比统计图';
      this.$refs.databaseType.refresh()
    },
  }
}
</script>

<style lang="less" scoped>
.card-container{
  &:after{
    content: '';
    clear: both;
    display: block;
  }
  .card-list{
    float: left;
    width: 100%;
    margin-left: 4%;
    text-align: center;
    &:nth-child(1){
      margin-left: 0;
    }
    .card-content{
      width:25%;
      float:left;
      display: inline-block;
      text-align: center;
      font-size: 18px;
      font-weight: bold;
      height: 42px;
    }
  }
}
</style>
