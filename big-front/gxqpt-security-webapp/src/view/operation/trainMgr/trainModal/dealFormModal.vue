<template>
    <Modal @on-visible-change="openModal" v-model="evalModal" title="服务报告" width="40%" :closable='false' :mask-closable="false">
        <div style="text-align:center">
            <Form ref="evalData" :model="evalData" :rules="ruleValidate" :label-width="70" class="clearfix">
                <Row :gutter="40">
                    <Col span="9">
                        <FormItem label="时间" label-position="left" prop="date">
                            <DatePicker v-model="evalData.date" type="date" placeholder="请选择时间" style="width: 200px"></DatePicker>
                        </FormItem>
                    </Col>
                     <Col span="24">
                        <FormItem label="服务过程" :label-width="72" prop="process">
                            <Input v-model="evalData.process" type="textarea" :autosize="{minRows: 2,maxRows: 5}"></Input>
                        </FormItem>
                    </Col>
                     <Col span="24">
                        <FormItem label="结果" :label-width="72" prop="results">
                            <Input v-model="evalData.results" type="textarea" :autosize="{minRows: 2,maxRows: 5}"></Input>
                        </FormItem>
                    </Col>
                     <Col span="24">
                        <FormItem label="备注" :label-width="72" prop="note">
                            <Input v-model="evalData.note" type="textarea" :autosize="{minRows: 2,maxRows: 5}"></Input>
                        </FormItem>
                    </Col>
                     <Col span="12">
                        <FormItem label="服务报告" prop="evalReport">
                            <Upload  ref="evalReport" :on-success="setEvalReport" :on-remove="removeEvalReport" multiple class="left" action="//jsonplaceholder.typicode.com/posts/">
                                <Button type="ghost" icon="ios-cloud-upload-outline">选择文件</Button>
                            </Upload>
                        </FormItem>
                    </Col>
                    <Col span="12">
                        <FormItem label="服务成果" prop="reformReport">
                            <Upload ref="reformReport" :on-success="setReformReport" :on-remove="removeReformReport" multiple class="left" action="//jsonplaceholder.typicode.com/posts/">
                                <Button type="ghost" icon="ios-cloud-upload-outline">选择文件</Button>
                            </Upload>
                        </FormItem>
                    </Col>
                </Row>
            </Form>
        </div>
        <div slot="footer">
            <Button class="modalBtn" type="primary" @click="handleSubmit" size="large">确定</Button>
            <Button class="modalBtn" type="default" @click="closeModal" size="large">关闭</Button>
        </div>
    </Modal>
</template>

<script>
export default {
    data(){
        return{
            evalData: {
                safetyLevel: '3',
            },
            ruleValidate: {
            }
        }
    },
    props:{
        evalModal:{
            type: Boolean,
            default: false
        },
        closeModal: Function,
    },
    methods:{
        openModal(status) {
            if(!status){
                this.$refs['evalData'].resetFields();
            }
        },
        handleSubmit() {
            this.$refs['evalData'].validate((valid) => {
                if(valid) {
                    this.save();
                } else {
                    this.$Message.error('表单验证失败!');
                }
            })
        },
        setEvalReport(response, file, fileList) {
            this.evalData.evalReport = 'ok';
            this.validForm();
        },
        setReformReport(response, file, fileList) {
            this.evalData.reformReport = 'ok';
            this.validForm();
        },
        removeEvalReport(file, fileList) {
            if(fileList.length) {
                this.evalData.evalReport = 'ok';
            } else {
                this.evalData.evalReport = '';
            }
            this.validForm();
        },
        removeReformReport(file, fileList) {
            if(fileList.length) {
                this.evalData.reformReport = 'ok';
            } else {
                this.evalData.reformReport = '';
            }
            this.validForm();
        },
        validForm() {
            this.$refs['evalData'].validate();
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
