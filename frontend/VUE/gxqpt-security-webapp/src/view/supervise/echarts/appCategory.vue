<!-- 各应用预警类别情况 -->
<template>
  <div id="appCategory"></div>
</template>

<script>
let appCategory = '';
export default {
  data () {
    return{
      nameList: [],
      indicator: [],
      echartData: [],
      appCategory: '',
    }
  },
  methods: {
    initAppCategory() {
      if(this.indicator.length==0){
        this.indicator = [0,0,0,0,0,0,0,0];
        this.nameList = ['暂无数据'];
    	};
      appCategory = '';
      // 基于准备好的dom，初始化echarts实例
      if (!appCategory) {
        appCategory = this.$echarts.init(document.getElementById('appCategory'))
      }
      const option = {
        backgroundColor: '#fff',
        title: {
          show: false
        },
        tooltip: {
          show: true
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          top: '10%',
          right: '10%',
          data: this.nameList
        },
        radar: {
          shape: 'circle',
          scale: true,
          indicator: this.indicator,
          center: ['30%', '50%'],
          radius: '50%'
        },
        series: [{
          type: 'radar',
          zlevel: 2,
          data: this.echartData
        }]
      }
      // 使用刚指定的配置项和数据显示图表。
      appCategory.setOption(option)
    },
    count(){//整理数据
      let obj = {};
      console.log(this.echartData);
      for(var i =0;i<this.echartData.length;i++){
        Object.assign(obj,this.echartData[i].count);//合并选项
        this.echartData[i].value = [];
        this.nameList.push(this.echartData[i].name);
      }
      let maxArray = []
      this.echartData.map(item => {
        Object.values(item.count).map((res, index) => {
          maxArray.push(res)
        })
      })
      let maxVal = Math.max(...maxArray)
      for(var j in obj){//计算全部显示数据
        this.indicator.push({'text':j, 'max': maxVal + 5});
      }
      for(var i =0;i<this.echartData.length;i++){//计算雷达数据，没有则为0
        for(var j in obj){
          (this.echartData[i].count[j]) ? this.echartData[i].value.push(this.echartData[i].count[j]) : this.echartData[i].value.push(0);
        }
      }
    },
    refresh (data) {
      Object.assign(this.$data, this.$options.data())
      this.$nextTick(() => {
        this.echartData = data
        this.count()
        this.initAppCategory()
      })
    }
  }
}
</script>

<style lang="less" scoped>
#appCategory {
  width: 100%;
  height: 300px;
}
</style>
