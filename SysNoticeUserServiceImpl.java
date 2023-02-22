package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysNoticeUser;
import com.ruoyi.system.mapper.SysNoticeUserMapper;
import com.ruoyi.system.service.ISysNoticeUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户消息提醒Service业务层处理
 *
 * @author ruoyi
 * @date 2023-01-03
 */
@AllArgsConstructor
@Service
public class SysNoticeUserServiceImpl extends ServiceImpl<SysNoticeUserMapper, SysNoticeUser> implements ISysNoticeUserService {
    
    private SysNoticeUserMapper sysNoticeUserMapper;
    
    /**
     * 查询系统用户消息提醒列表
     *
     * @param sysNoticeUser 系统用户消息提醒
     * @return 系统用户消息提醒
     */
    @Override
    public List<SysNoticeUser> selectSysNoticeUserList(SysNoticeUser sysNoticeUser) {
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        sysNoticeUser.setUserId(userId);
        return sysNoticeUserMapper.selectList(new LambdaQueryWrapper<>(sysNoticeUser));
    }
    
    /**
     * 新增消息提醒
     *
     * @param noticeList
     * @return
     */
    @Override
    @Transactional
    public boolean addNoticeList(List<SysNoticeUser> noticeList) {
        boolean first = true;
        Long initiatorId = null;
        if (ObjectUtil.isNotEmpty(noticeList)) {
            List<SysNoticeUser> existList = this.list(new LambdaQueryWrapper<SysNoticeUser>().eq(SysNoticeUser::getProcInsId, noticeList.get(0).getProcInsId()));
            for (SysNoticeUser sysNoticeUser : noticeList) {
                //如果是第一次添加，先判断是否已存在该流程的提醒信息，如果不存在，则说明当前节点为申请人节点，不添加消息提醒
                if (first) {
                    first = false;
                    if (ObjectUtil.isEmpty(existList)) {
                        initiatorId = sysNoticeUser.getUserId();
                        continue;
                    } else {
                        initiatorId = existList.get(0).getInitiatorId();
                    }
                }
                sysNoticeUser.setInitiatorId(initiatorId);
                sysNoticeUserMapper.insert(sysNoticeUser);
                //可考虑在此使用websocket向页面发送消息进行即时提醒
            }
            return true;
        }
        return false;
    }
    
    /**
     * 特殊处理，添加备注
     * @param userLogCategoryCode 日志分类
     * @param taskId
     * @param remark
     */
    @Override
    public void handleNoticeUser(String userLogCategoryCode, String taskId, String remark) {
        this.handleNoticeUser(userLogCategoryCode, null, null, taskId, null, remark);
    }
    
    /**
     * 处理消息提醒
     *
     * @param userLogCategoryCode 日志分类
     * @param dataId          数据在表中的id
     * @param userId          用户id
     * @param taskId          任务id
     * @param extraMark       额外标识
     * @return
     */
    @Override
    @Transactional
    public void handleNoticeUser(@NotNull String userLogCategoryCode, String dataId, Long userId, String taskId, String extraMark, String remark) {
        try {
            SysNoticeUser handlingNotice = sysNoticeUserMapper.selectOne(new LambdaQueryWrapper<SysNoticeUser>()
                    .eq(ObjectUtil.isNotEmpty(userLogCategoryCode), SysNoticeUser::getUserLogCategory, userLogCategoryCode)
                    .eq(ObjectUtil.isNotEmpty(dataId), SysNoticeUser::getDataId, dataId)
                    .eq(ObjectUtil.isNotEmpty(taskId), SysNoticeUser::getTaskId, taskId)
                    .eq(ObjectUtil.isNotEmpty(userId), SysNoticeUser::getUserId, userId)
                    .eq(ObjectUtil.isNotEmpty(extraMark), SysNoticeUser::getExtraMark, extraMark)
                    .eq(SysNoticeUser::getHandleType, 0));
            if (ObjectUtil.isEmpty(handlingNotice)) return;
            sysNoticeUserMapper.handleNotice(handlingNotice.getId(), remark);
            
            if (ObjectUtil.equals(handlingNotice.getHandleType(), 1)) {//如果是独占模式，则需要将其他处于同一任务的消息提醒进行删除
                List<SysNoticeUser> deletingNotices = sysNoticeUserMapper.selectList(new LambdaQueryWrapper<SysNoticeUser>()
                        .eq(ObjectUtil.isNotEmpty(userLogCategoryCode), SysNoticeUser::getUserLogCategory, userLogCategoryCode)
                        .eq(ObjectUtil.isNotEmpty(dataId), SysNoticeUser::getDataId, dataId)
                        .eq(ObjectUtil.isNotEmpty(extraMark), SysNoticeUser::getExtraMark, extraMark));
                List<Long> deletingIdList = deletingNotices.stream().map(SysNoticeUser::getId).collect(Collectors.toList());
                sysNoticeUserMapper.deleteBatchIds(deletingIdList);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException("处理消息提醒失败！错误信息：" + e.getMessage());
        }
    }
    
    /**
     * 删除系统用户消息提醒信息
     *
     * @param procInsId 流程实例ID
     * @return
     */
    @Override
    public boolean deleteByProcInsId(String procInsId) {
        return this.remove(new LambdaQueryWrapper<SysNoticeUser>().eq(SysNoticeUser::getProcInsId, procInsId));
    }
    
}
