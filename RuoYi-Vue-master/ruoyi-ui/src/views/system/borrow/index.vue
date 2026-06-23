<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="读者编号" prop="rdID">
        <el-input
          v-model="queryParams.rdID"
          placeholder="请输入读者编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="图书序号" prop="bkID">
        <el-input
          v-model="queryParams.bkID"
          placeholder="请输入图书序号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实际还书日期" prop="ldDateRetAct">
        <el-date-picker clearable
          v-model="queryParams.ldDateRetAct"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择实际还书日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="超期天数" prop="ldOverDay">
        <el-input
          v-model="queryParams.ldOverDay"
          placeholder="请输入超期天数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="超期金额" prop="ldOverMoney">
        <el-input
          v-model="queryParams.ldOverMoney"
          placeholder="请输入超期金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="罚款金额" prop="ldPunishMoney">
        <el-input
          v-model="queryParams.ldPunishMoney"
          placeholder="请输入罚款金额"
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
          v-hasPermi="['system:borrow:add']"
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
          v-hasPermi="['system:borrow:edit']"
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
          v-hasPermi="['system:borrow:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:borrow:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="borrowList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="读者证号" align="center" prop="rdCode" />/
      <el-table-column label="图书序号" align="center" prop="bkID" />

      <el-table-column label="借书日期" align="center" prop="ldDateOut" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.ldDateOut, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="应还日期" align="center" prop="ldDateRetPlan" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.ldDateRetPlan, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实际还书日期" align="center" prop="ldDateRetAct" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.ldDateRetAct, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="超期天数" align="center" prop="ldOverDay" />
      <el-table-column label="超期金额" align="center" prop="ldOverMoney" />
      <el-table-column label="罚款金额" align="center" prop="ldPunishMoney" />

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:borrow:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:borrow:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="success"
            icon="el-icon-circle-check"
            v-if="scope.row.isHasReturn == 0"
            @click="handleSelfReturn(scope.row)"
          >归还</el-button>
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

    <!-- 添加或修改借阅记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="读者证号" prop="rdCode">
          <el-input v-model="form.rdCode" placeholder="请输入读者证号" />
        </el-form-item>
        <el-form-item label="图书序号" prop="bkID">
          <el-input v-model="form.bkID" placeholder="请输入图书序号" />
        </el-form-item>
        <el-form-item label="续借次数" prop="ldContinueTimes">
          <el-input v-model="form.ldContinueTimes" placeholder="请输入续借次数" />
        </el-form-item>
        <el-form-item label="借书日期" prop="ldDateOut">
          <el-date-picker clearable
            v-model="form.ldDateOut"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择借书日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="应还日期" prop="ldDateRetPlan">
          <el-date-picker clearable
            v-model="form.ldDateRetPlan"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择应还日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="实际还书日期" prop="ldDateRetAct">
          <el-date-picker clearable
            v-model="form.ldDateRetAct"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择实际还书日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="超期天数" prop="ldOverDay">
          <el-input v-model="form.ldOverDay" placeholder="请输入超期天数" />
        </el-form-item>
        <el-form-item label="超期金额" prop="ldOverMoney">
          <el-input v-model="form.ldOverMoney" placeholder="请输入超期金额" />
        </el-form-item>
        <el-form-item label="罚款金额" prop="ldPunishMoney">
          <el-input v-model="form.ldPunishMoney" placeholder="请输入罚款金额" />
        </el-form-item>
        <el-form-item label="是否已还(0未还,1已还)" prop="IsHasReturn">
          <el-input v-model="form.IsHasReturn" placeholder="请输入是否已还(0未还,1已还)" />
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
import { listBorrow, getBorrow, delBorrow, addBorrow, updateBorrow } from "@/api/system/borrow"
import { selfReturn } from "@/api/system/borrow";

export default {
  name: "Borrow",
  data() {
    return {
      // 遮罩层
      loading: true,
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
      // 借阅记录表格数据
      borrowList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        rdID: null,
        bkID: null,
        ldContinueTimes: null,
        ldDateOut: null,
        ldDateRetPlan: null,
        ldDateRetAct: null,
        ldOverDay: null,
        ldOverMoney: null,
        ldPunishMoney: null,
        IsHasReturn: null,
        OperatorBorrow: null,
        OperatorReturn: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询借阅记录列表 */
    getList() {
      this.loading = true
      listBorrow(this.queryParams).then(response => {
        this.borrowList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        BorrowID: null,
        rdID: null,
        rdCode: null,
        bkID: null,
        ldContinueTimes: null,
        ldDateOut: null,
        ldDateRetPlan: null,
        ldDateRetAct: null,
        ldOverDay: null,
        ldOverMoney: null,
        ldPunishMoney: null,
        IsHasReturn: null,
        OperatorBorrow: null,
        OperatorReturn: null
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
      this.ids = selection.map(item => item.borrowID)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加借阅记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const BorrowID = row.borrowID || this.ids
      getBorrow(BorrowID).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改借阅记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.BorrowID != null) {
            updateBorrow(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addBorrow(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },

    handleSelfReturn(row) {
      // 1. 自动寻找正确的 ID (兼容 borrowID, BorrowID, ldID)
      const id = row.borrowID || row.BorrowID || row.ldID;

      if (!id) {
        this.$modal.msgError("错误：无法获取借阅ID，请检查前端字段名！");
        return;
      }

      this.$modal.confirm('确认归还这本书吗').then(function() {
        // 2. 发送请求给后端
        return selfReturn({ borrowID: id });
      }).then(() => {
        this.getList(); // 刷新列表
        this.$modal.msgSuccess("归还成功！");
      }).catch(() => {});
    },

       /** 删除按钮操作 */
  handleDelete(row) {
    // 1. 定义一个变量存 ID
    let borrowId = null;

    // 2. 智能侦探逻辑：看看传进来的 row 到底是个啥？
    if (row && row.borrowID) {
      // 情况A：是行数据（且字段名是 borrowID）
      borrowId = row.borrowID;
    } else if (row && row.borrowId) {
      // 情况B：是行数据（且字段名是 borrowId - 兼容写法）
    borrowId = row.borrowId;
    } else {
      // 情况C：row 是 PointerEvent (鼠标事件) 或者 undefined
      // 说明你点了顶部的删除，或者 scope 没传进来
      // 这时候我们要去取“勾选框”选中的 ID (this.ids)
      borrowId = this.ids;
    }

    // 3. 打印最终结果 (F12看控制台)
    console.log("【最终判定】当前操作的ID是:", borrowId);

    // 4. 安全拦截：如果没有 ID，弹窗提示，不发请求
    if (!borrowId || borrowId == 0 || (Array.isArray(borrowId) && borrowId.length === 0)) {
      this.$modal.msgWarning("请先勾选要删除的记录，或点击行后的删除按钮！");
      return;
    }

  // 5. 发起确认和请求
  this.$modal.confirm('是否确认删除借阅记录编号为"' + borrowId + '"的数据项？').then(function() {
    return delBorrow(borrowId);
  }).then(() => {
    this.getList();
    this.$modal.msgSuccess("删除成功");
  }).catch(() => {});
},

    /** 导出按钮操作 */
    handleExport() {
      this.download('system/borrow/export', {
        ...this.queryParams
      }, `borrow_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
