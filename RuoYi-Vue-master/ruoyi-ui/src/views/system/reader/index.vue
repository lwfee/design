<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="读者证号" prop="rdCode">
        <el-input
          v-model="queryParams.rdCode"
          placeholder="请输入读者证号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="读者姓名" prop="rdName">
        <el-input
          v-model="queryParams.rdName"
          placeholder="请输入读者姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="性别" prop="rdSex">
        <el-select
          v-model="queryParams.rdSex"
          placeholder="请选择性别"
          clearable
          size="small"
        >
          <el-option label="男" value="男" />
          <el-option label="女" value="女" />
        </el-select>
      </el-form-item>
      <el-form-item label="单位名称" prop="rdDept">
        <el-input
          v-model="queryParams.rdDept"
          placeholder="请输入单位名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="电话号码" prop="rdPhone">
        <el-input
          v-model="queryParams.rdPhone"
          placeholder="请输入电话号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="读者类别" prop="rdType">
        <el-select
          v-model="queryParams.rdType"
          placeholder="请选择读者类别"
          clearable
          size="small"
        >
          <el-option
            v-for="item in readerTypeOptions"
            :key="item.rdType"
            :label="item.rdTypeName"
            :value="item.rdType"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="电子邮箱" prop="rdEmail">
        <el-input
          v-model="queryParams.rdEmail"
          placeholder="请输入电子邮箱"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="办证日期" prop="rdDateReg">
        <el-date-picker clearable
          v-model="queryParams.rdDateReg"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择办证日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="证件状态" prop="rdStatus">
        <el-select v-model="queryParams.rdStatus" placeholder="请选择状态" clearable size="small">
          <el-option label="有效" :value="1" />
          <el-option label="挂失" :value="2" />
          <el-option label="注销" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="已借书数量" prop="rdBorrowQty">
        <el-input
          v-model="queryParams.rdBorrowQty"
          placeholder="请输入已借书数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:reader:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:reader:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:reader:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:reader:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="readerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="读者证号" align="center" prop="rdCode" />
      <el-table-column label="读者姓名" align="center" prop="rdName" />
      <el-table-column label="性别" align="center" prop="rdSex" />
      <el-table-column label="读者类别" align="center" prop="rdType" :formatter="formatReaderType" />
      <el-table-column label="单位名称" align="center" prop="rdDept" />
      <el-table-column label="电话号码" align="center" prop="rdPhone" />
      <el-table-column label="电子邮箱" align="center" prop="rdEmail" />
      <el-table-column label="办证日期" align="center" prop="rdDateReg" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.rdDateReg, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>

      <el-table-column label="证件状态" align="center" prop="rdStatus" >
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.rdStatus == 1">有效</el-tag>

          <el-tag type="warning" v-else-if="scope.row.rdStatus == 2">挂失</el-tag>

          <el-tag type="info" v-else-if="scope.row.rdStatus == 0">注销</el-tag>

          <span v-else>{{ scope.row.rdStatus }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已借书数量" align="center" prop="rdBorrowQty" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:reader:edit']"
          >修改</el-button>

          <el-button
            v-if="scope.row.rdStatus !== '1'"
            size="mini"
            type="text"
            icon="el-icon-refresh-right"
            @click="handleReissue(scope.row)"
          >补办</el-button>

          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:reader:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改读者信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">


        <el-form-item label="读者姓名" prop="rdName">
          <el-input v-model="form.rdName" placeholder="请输入读者姓名" />
        </el-form-item>
        <el-form-item label="读者类别" prop="rdType">
          <el-select v-model="form.rdType" placeholder="请选择读者类别">
            <el-option
              v-for="item in readerTypeOptions"
              :key="item.rdType"
              :label="item.rdTypeName"
              :value="item.rdType"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="性别" prop="rdSex">
          <el-select v-model="form.rdSex" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="证件状态" prop="rdStatus">
          <el-select v-model="form.rdStatus" placeholder="请选择证件状态">
            <el-option label="有效" value="1" />
            <el-option label="挂失" value="2" />
            <el-option label="注销" value="0" />
          </el-select>        </el-form-item>
        <el-form-item label="单位名称" prop="rdDept">
          <el-input v-model="form.rdDept" placeholder="请输入单位名称" />
        </el-form-item>
        <el-form-item label="电话号码" prop="rdPhone">
          <el-input v-model="form.rdPhone" placeholder="请输入电话号码" />
        </el-form-item>
        <el-form-item label="电子邮箱" prop="rdEmail">
          <el-input v-model="form.rdEmail" placeholder="请输入电子邮箱" />
        </el-form-item>
        <el-form-item label="办证日期" prop="rdDateReg">
          <el-date-picker clearable
            v-model="form.rdDateReg"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择办证日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="已借书数量" prop="rdBorrowQty">
          <el-input v-model="form.rdBorrowQty" placeholder="请输入已借书数量" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listReader, getReader, delReader, addReader, updateReader, getNextCode, reissueReaderCard } from "@/api/system/reader"
import { listReadertype } from "@/api/system/readertype"
import {parseTime} from "../../../utils/ruoyi";

export default {
  name: "Reader",
  data() {
    // ================== 1. 名字查重校验 ==================
const validateRdName = (rule, value, callback) => {
  if (!value) return callback(new Error("读者姓名不能为空"));

  // 调用通用查重逻辑：传参 { rdName: value }, 报错提示 "姓名"
  checkDuplicate({ rdName: value }, "姓名", callback);
};

// ================== 2. 证号查重校验 ==================
const validateRdCode = (rule, value, callback) => {
  if (!value) return callback(new Error("读者证号不能为空"));

  // 调用通用查重逻辑：传参 { rdCode: value }, 报错提示 "证号"
  checkDuplicate({ rdCode: value }, "证号", callback);
};

// ================== 3. 通用查重核心逻辑 (提取出来避免写两遍) ==================
// 注意：这里要用箭头函数以继承 this
const checkDuplicate = (queryParams, fieldName, callback) => {
  listReader(queryParams).then(response => {
    // A. 获取输入框的值 (queryParams里唯一的那个值)
    const inputValue = Object.values(queryParams)[0];

    // B. 精确过滤 (防止模糊查询干扰)
    // 比如搜 100，后台可能把 1001 也返给你，必须在前端再筛一遍
    const targetField = fieldName === "姓名" ? "rdName" : "rdCode";
    const exactMatch = response.rows.find(item => item[targetField] === inputValue);

    if (exactMatch) {
      // C. 既然查到了，就要看 ID 是不是我自己

      // 获取数据库里的 ID (兼容大小写)
      const dbId = exactMatch.rdID || exactMatch.rdId;
      // 获取当前表单的 ID
      const formId = this.form.rdID || this.form.rdId;

      console.log(`正在查${fieldName}:`, inputValue, "数据库ID:", dbId, "当前表单ID:", formId);

      // D. 判决时刻
      if (formId && dbId == formId) {
        // ID 存在且相等 -> 是我自己改我自己 -> 【通过】
        callback();
      } else {
        // ID 不相等 -> 是别人用了这个值 -> 【报错】
        callback(new Error(`该读者${fieldName}已存在，不允许重复！`));
      }
    } else {
      // E. 根本没查到 -> 没人用 -> 【通过】
      callback();
    }
  });
};
    return {
      // 遮罩层
      loading: true,
      // 存放所有的读者类别
      readerTypeOptions: [],
      // 选中数组
      ids: [],
      selection: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 读者信息表格数据
      readerList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        rdName: null,
        rdSex: null,
        rdType: null,
        rdDept: null,
        rdPhone: null,
        rdEmail: null,
        rdDateReg: null,
        rdPhoto: null,
        rdStatus: null,
        rdBorrowQty: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        rdName: [
        { required: true, message: "读者姓名不能为空", trigger: "blur" },
        //引用上面的校验函数
        { validator: validateRdName, trigger: "blur" }
      ],
        rdSex: [
          // 下拉框或单选框，建议用 trigger: 'change'（一选就触发）
          { required: true, message: "请选择性别", trigger: "change" }
        ],
        // 3. 读者类别（新增必填）
        rdType: [
          { required: true, message: "请选择读者类别", trigger: "change" }
        ],

      }
    }
  },
  created() {
    this.getList()
    this.getReaderTypeList();
  },
  methods: {
    parseTime,
    /** 查询读者信息列表 */
    getList() {
      this.loading = true
      listReader(this.queryParams).then(response => {
        this.readerList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 翻译函数
    formatReaderType(row, column) {
      // 这里的 row.rdType 就是那个 "1"
      const found = this.readerTypeOptions.find(item => item.rdType == row.rdType);
      // 如果找到了就返回名字，找不到就还显示 ID
      return found ? found.rdTypeName : row.rdType;
    },
    /** 查询所有读者类别（复用分页接口） */
    getReaderTypeList() {
    // 构造查询参数
      const queryParams = {
        pageNum: 1,
      // 核心技巧：设置一个足够大的数，确保能覆盖所有类别
        pageSize: 1000,
      // 如果后端有排序需求，也可以在这里加 orderByColumn 等参数
    };
      listReadertype(queryParams).then(response => {
      // 若依的标准返回格式通常是 { rows: [...], total: ... }
      // 我们只需要 rows 数据
        this.readerTypeOptions = response.rows;
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
        rdID: null,
        rdName: null,
        rdSex: null,
        rdType: null,
        rdDept: null,
        rdPhone: null,
        rdEmail: null,
        rdDateReg: null,
        rdPhoto: null,
        rdStatus: "1",
        rdBorrowQty: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.rdID)
      this.selection = selection;
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */

    handleAdd() {
      this.reset(); // 1. 先重置表单，确保 status: "0" 等默认值生效

      const params = { _t: new Date().getTime() };

      // 2. 调用后端接口 (需确保已在 api 文件中 import getNextCode)
      getNextCode().then(response => {
        // 3. 将后端返回的下一个编号（如 "rd11"）赋给表单对象
        // response.msg 对应后端 AjaxResult.success 的第二个参数
        this.form.rdCode = response.data;

        // 4. 填充完毕后再打开弹窗
        this.open = true;
        this.title = "添加读者信息";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const rdID = row.rdID || this.ids
      getReader(rdID).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改读者信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.rdID != null) {
            updateReader(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addReader(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const rdIDs = row.rdID || this.ids
      const rdCode = row.rdCode || this.selection.map(item => item.rdCode).join(",");
      this.$modal.confirm('是否确认删除读者信息编号为"' + rdCode + '"的数据项？').then(function() {
        return delReader(rdIDs)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/reader/export', {
        ...this.queryParams
      }, `reader_${new Date().getTime()}.xlsx`)
    },

    /** 补办按钮操作 */
    handleReissue(row) {
      const rdID = row.rdID;
      const oldCode = row.rdCode;

      this.$modal.confirm('确认要为读者【' + row.rdName + '】补办证件吗？原证号 ' + oldCode + ' 将作废并生成新号。').then(() => {
        // 调用后端补办接口，只需传 rdID
        return reissueReaderCard({ rdID: rdID });
      }).then(() => {
        this.getList(); // 刷新列表
        this.$modal.msgSuccess("补办成功");
      }).catch(() => {});
    }

  }
}
</script>
