<template>
  <Layout>
    <Content>
      <Breadcrumb>
        <BreadcrumbItem>运维分析</BreadcrumbItem>
        <BreadcrumbItem>共性应用监控</BreadcrumbItem>
      </Breadcrumb>
      <Card>
        <Row type="flex" justify="space-between">
          <Col style="width: 49%">
            <chart-card title="访问量">
              <chartLine ref="appChart1" el="appChart1" :lineOption="appChart1Option">
                <div id="appChart1" style="height: 400px;"></div>
              </chartLine>
            </chart-card>
          </Col>
          <Col style="width: 49%">
            <chart-card title="故障平均处理时长">
              <chartBar ref="appChart2" el="appChart2" :barOption="appChart2Option">
                <div id="appChart2" style="height: 400px;"></div>
              </chartBar>
            </chart-card>
          </Col>
        </Row>
        <Row type="flex" justify="space-between">
          <Col style="width: 49%">
            <chart-card title="故障处理分析">
              <chartBar ref="appChart3" el="appChart3" :barOption="appChart3Option">
                <div id="appChart3" style="height: 400px;"></div>
              </chartBar>
            </chart-card>
          </Col>
          <Col style="width: 49%">
            <chart-card title="故障率">
              <chartLine ref="appChart4" el="appChart4" :lineOption="appChart4Option">
                <div id="appChart4" style="height: 400px;"></div>
              </chartLine>
            </chart-card>
          </Col>
        </Row>
      </Card>
    </Content>
  </Layout>
</template>

<script>
import {mapState} from 'vuex'
import api from '@/api/axiosApi'
import operationApiList from '@/api/operationApiList'
// echart图外层容器，包括卡片样式
import chartCard from '@/view/home/chartCard.vue'
import chartLine from './../echarts/ChartLine.vue'
import chartBar from './../echarts/bar.vue'
// echart图的一些共用的配置
import {symbolLine, symbolBar} from '@/assets/js/echartOption'

export default {
  components: {
    chartCard,
    chartLine,
    chartBar
  },
  data () {
    return {
      appChart1Option: symbolBar(),
      appChart2Option: symbolLine(),
      appChart3Option: symbolBar(),
      appChart4Option: symbolLine(),
      commonXdata: []
    }
  },
  computed: {
    ...mapState(['userInfo'])
  },
  mounted() {
    this.loginAmount()
    this.appAnalysis()
  },
  methods: {
    // 获取访问量
    loginAmount () {
      const vm = this
      api(operationApiList.loginAmount)
      .then(res => {
        if (res.data.errcode === 0) {
          const data = res.data.data
          vm.drawAppChart1(data.loginAmount, data.yMonthArray)
        } else {
          vm.$Message.info(res.data.errmsg)
        }
      }, err => {console.log(err)})
    },
    // 获取故障平均处理时长、故障处理分析、故障率
    appAnalysis() {
      const vm = this
      api(operationApiList.appAnalysis)
      .then(res => {
        if (res.data.errcode === 0) {
          const data = res.data.data
          vm.commonXdata = data.yMonthArray
          vm.drawAppChart2(data.faultAvgList)
          vm.drawAppChart3(data.faultNumList, data.faultDealList)
          vm.drawAppChart4(data.faultRateList)
        } else {
          vm.$Message.info(res.data.errmsg)
        }
      }, err => {console.log(err)})
    },
    // 访问量
    drawAppChart1 (yData, xData) {
      this.appChart1Option.xAxis.data = xData
      this.appChart1Option.series[0].barWidth = 30
      this.appChart1Option.series[0].symbolSize = ['100%', 4]
      this.appChart1Option.series[0].data = yData
      this.appChart1Option.series[0].name = '访问量'
      this.$refs.appChart1.refresh()
    },
    // 故障平均处理时长
    drawAppChart2 (yData) {
      this.appChart2Option.xAxis.data = this.commonXdata
      this.appChart2Option.yAxis.name = '小时'
      this.appChart2Option.series[0].data = yData
      this.$refs.appChart2.refresh()
    },
    // 故障处理分析
    drawAppChart3 (faultNumList, faultDealList) {
      this.appChart3Option.legend = {
        data: ['故障产生量', '故障处理量']
      },
      this.appChart3Option.xAxis.data = this.commonXdata
      this.appChart3Option.series[0].barWidth = 20
      this.appChart3Option.series[0].symbolSize = ['50%', 4]
      this.appChart3Option.series[0].data = faultNumList
      this.appChart3Option.series[0].name = '故障产生量'
      this.appChart3Option.series.push({
        name: '故障处理量',
        type: 'pictorialBar',
        barGap: 0.1,
        barWidth: 20,
        label: {
          show: true,
          position: 'top',
          fontSize: 20,
          color: '#000'
        },
        // itemStyle: {
        //   normal: {
        //     color: '#0f375f'
        //   }
        // },
        symbol: 'rect',
        symbolRepeat: true,
        symbolSize: ['50%', 4],
        symbolMargin: 0,
        data: faultDealList
      })
      this.$refs.appChart3.refresh()
    },
    // 故障率
    drawAppChart4 (faultRateList) {
      this.appChart4Option.yAxis.name = ''
      this.appChart4Option.yAxis.axisLabel = {
        formatter: '{value}%'
      }
      this.appChart4Option.xAxis.data = this.commonXdata
      this.appChart4Option.series[0].data = faultRateList
      this.$refs.appChart4.refresh()
    }
  }
}
</script>
