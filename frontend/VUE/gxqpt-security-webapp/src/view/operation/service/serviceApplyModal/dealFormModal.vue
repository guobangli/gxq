<template>
    <Modal @on-visible-change="openModal" v-model="evalModal" title="处理" width="720" :closable='false' :mask-closable='false'>
        <div style="text-align:center">
            <Form ref="evalData" :model="docData" :rules="ruleValidate" :label-width="70" class="clearfix">
                <Row :gutter="40">
                    <Col span="9">
                        <FormItem label="时间" label-position="left" prop="reportTime">
                            <DatePicker v-model="docData.reportTime"  :options="options" @on-change="changeTime" type="date" placeholder="请选择时间" style="width: 200px"></DatePicker>
                        </FormItem>
                    </Col>
                     <Col span="24">
                        <FormItem label="服务过程" :label-width="72" prop="serviceProcess">
                            <Input v-model="docData.serviceProcess" type="textarea" :autosize="{minRows: 2,maxRows: 5}" :maxlength="1000" placeholder="限制输入1000个字符以内"></Input>
                        </FormItem>
                    </Col>
                     <Col span="24">
                        <FormItem label="结果" :label-width="72" prop="result">
                            <Input v-model="docData.result" type="textarea" :autosize="{minRows: 2,maxRows: 5}" :maxlength="1000" placeholder="限制输入1000个字符以内"></Input>
                        </FormItem>
                    </Col>
                     <Col span="24">
                        <FormItem label="备注" :label-width="72">
                            <Input v-model="docData.remark" type="textarea" :autosize="{minRows: 2,maxRows: 5}" :maxlength="1000" placeholder="限制输入1000个字符以内"></Input>
                        </FormItem>
                    </Col>
                    <Col span="24">
                        <FormItem label="服务报告">
                        	<hy-upload ref="reportfile" :on-success="setEvalReport" :format="['xls','xlsx','doc', 'docx','pdf']" :on-remove="removeEvalReport"></hy-upload>
                        </FormItem>
                    </Col>
                    <Col span="24">
                        <FormItem label="服务成果">
                            <hy-upload ref="resultfile" :on-success="setEvalResult" :format="['xls','xlsx','doc', 'docx','pdf']" :on-remove="removeEvalReport"></hy-upload>
                        </FormItem>
                    </Col>
                </Row>
            </Form>
        </div>
        <div slot="footer">
            <Button class="modalBtn" type="default" @click="closeModal" size="large">关闭</Button>
            <Button class="modalBtn" type="primary" @click="handleSubmit" size="large">确定</Button>
        </div>
    </Modal>
</template>

<script>
import api from '@/api/axiosApi'
import operationApiList from '@/api/operationApiList'
import hyUpload from '@/components/hengyun/hyUpload'
export default {
    data(){
        return{
        	docData: {},
            reportFile: [],
            resultFile: [],
            ruleValidate: {
            	reportTime: [{
					required: true,
					message: '请选择时间！',
					trigger: 'change'
				}],
				serviceProcess: [{
					required: true,
					message: '请填写服务过程！',
					trigger: 'blur'
				}],
				result: [{
					required: true,
					message: '请填写结果！',
					trigger: 'blur'
				}],
            },
            options: {
              disabledDate (date) {
                return date && date.valueOf() > Date.now() // 设置不可选日期
              }
            },
        }
    },
    props:{
        evalModal:{
            type: Boolean,
            default: false
        },
        closeModal: Function,
    },
    components:{
		hyUpload
	},
    methods:{
    	changeTime(val) { //选择时间
			if(val) {
				let time = val.replace(/\//g, "-");
				this.docData.reportTime = time + " 00:00:00";
			} else {
				this.docData.reportTime = "";
			}
		},
    	getId(id){//获取id;
			this.docData.id=id;
		},
		setEvalReport(response, file, fileList) {//服务报告上传成功后回调方法
			let _this = this;
			let obj = {};
			if(response) {
				obj.bussId = _this.docData.id
				obj.bussType = 'service_report'
				obj.fileName = response.data.list[0].submittedFileName
				obj.fileSize = response.data.list[0].size
				obj.fileType = response.data.list[0].ext
				obj.fileUrl = response.data.list[0].url
				this.reportFile.push(obj)
			}
		},
		setEvalResult(response, file, fileList) {//服务成果上传成功后回调方法
			let _this = this;
			let obj = {};
			if(response) {
				obj.bussId = _this.docData.id
				obj.bussType = 'service_result'
				obj.fileName = response.data.list[0].submittedFileName
				obj.fileSize = response.data.list[0].size
				obj.fileType = response.data.list[0].ext
				obj.fileUrl = response.data.list[0].url
				this.resultFile.push(obj)
			}
		},
		removeEvalReport(file, fileList) {//删除服务报告附件方法
			this.reportFile = [];
			this.reportFile = fileList;
		},
        removeEvalResult(file, fileList) {//删除服务报告附件方法
            this.resultFile = [];
            this.resultFile = fileList;
        },
        openModal() {
            if(!this.evalModal){
                this.$refs['evalData'].resetFields()
                this.docData.remark = ''
                this.$refs.reportfile.clearFiles()
                this.$refs.resultfile.clearFiles()
                this.reportFile = []
                this.resultFile = []
            }
        },
        handleSubmit() {
            this.$refs['evalData'].validate((valid) => {
                if(valid) {
                	this.docData.status=4;
                    api(operationApiList.updateServiceReport, {
                        fileDTOS: [...this.reportFile, ...this.resultFile],
                        ...this.docData
                    }).then((res) => {
						if(res.status == 200 && res.data.data) {
							this.closeModal();
						}
					}, (err) => {console.log(err)})
                };
            })
        },
    }
}
</script>

<style scoped>
.ivu-form-item >>> .ivu-form-item-content{
    height:auto;
}
.ivu-input-wrapper >>> textarea{
    resize: none;
}
.download,.download:hover{
    width: 100px;
    height: 32px;
    line-height: 30px;
    color: #000;
    float: left;
}
</style>
