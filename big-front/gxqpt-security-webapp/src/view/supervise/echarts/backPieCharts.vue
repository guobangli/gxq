<!-- 人员预警处理对比分析图 -->
<template>
	<div id="backPieCharts"></div>
</template>

<script>
	export default {
		data () {
			return {
				echartData: [],
				lineBar: '',
				monthData:[],
				handled:[],
				newCome:[],
				pending:[]
			}
		},
		methods: {
			initLineBar() {
				// 基于准备好的dom，初始化echarts实例
				if (!this.lineBar) {
					this.lineBar = this.$echarts.init(document.getElementById('backPieCharts'))
				}
				const option = {
					color: ['#3186e2', '#37a2da', '#FFA358', '#FD7F7F', '#55CFF4', '#FFD851'],
					title: {
						text: '预警处理情况',
						subtext: '',
						x: 'center',
						top: '20px'
					},
          tooltip: {
              show: true,
              trigger: 'item',
              formatter: '{a}{b}:{c}'
          },
					angleAxis: {
						type: 'category',
						data: [this.monthData],
						z: 10,
						axisLine: {
              show: false
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              show: false  
            },
            splitLine: {
              show: false
            }
					},
					radiusAxis: {
					  axisLine: {
              show: false
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              show: false  
            },
            splitLine: {
              show: false
            }
					},
					polar: {
						radius: '45%'
					},
					series: [{
						type: 'bar',
						data: this.handled,
						coordinateSystem: 'polar',
						name: '处理平均时长',
						stack: 'a'
					}, {
						type: 'bar',
						data: this.newCome,
						coordinateSystem: 'polar',
						name: '确认平均时长',
						stack: 'a'
					},{
            type: 'pie',
            name: '',
            radius: ['45%', '50%'],
            avoidLabelOverlap: false,
            label: {
              normal: {
                show: false,
                formatter: '{b}:{c}'
              }
            },
            labelLine: {
              normal: {
                show: false
              }
            },
            data: [{
              value: this.handled[0],
              name: '处理平均时长'
            }, {
              value: this.newCome[0],
              name: '确认平均时长'
            }],
            itemStyle: {
              normal: {
                borderWidth: 3,
                borderColor: '#ffffff',
              },
              emphasis: {
                borderWidth: 3,
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
        }],
        // legend: {
        //   show: true,
        //   data: ['已处理数量', '新增数量', '待处理数量'],
        //   left: 'center',
        //   bottom: '0'
        // }
				};

				// 使用刚指定的配置项和数据显示图表。
				this.lineBar.setOption(option);
			},
			refresh (data) {
        this.echartData = data;
				this.monthData = [];
				this.handled = [];
				this.newCome = [];
				// this.pending = [];
				for(var i = 0;i<this.echartData.length;i++){
					this.monthData.push(this.echartData[i].month.substring(4) + '月');
					this.handled.push(this.echartData[i].avgHandleTime);
					this.newCome.push(this.echartData[i].avgConfirmTime);
					// this.pending.push(this.echartData[i].pending);
        }
        console.log(this.handled)
        console.log(this.newCome)
        console.log(this.pending)
				this.initLineBar()
			}
		}
	}
</script>

<style lang="less" scoped>
	#backPieCharts {
		width: 100%;
		height: 100%;
	}
</style>