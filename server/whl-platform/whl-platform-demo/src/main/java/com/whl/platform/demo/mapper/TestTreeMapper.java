package com.whl.platform.demo.mapper;

import com.whl.framework.mybatis.annotation.DataColumn;
import com.whl.framework.mybatis.annotation.DataPermission;
import com.whl.framework.mybatis.core.mapper.BaseMapperPlus;
import com.whl.platform.demo.domain.TestTree;
import com.whl.platform.demo.domain.vo.TestTreeVo;

/**
 * 测试树表Mapper接口
 *
 * @author Lion Li
 * @date 2021-07-26
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "dept_id"),
    @DataColumn(key = "userName", value = "user_id")
})
public interface TestTreeMapper extends BaseMapperPlus<TestTree, TestTreeVo> {

}
