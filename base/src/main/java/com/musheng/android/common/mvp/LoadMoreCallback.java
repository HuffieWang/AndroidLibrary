package com.musheng.android.common.mvp;

import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/30 14:18
 * Description :
 */
public interface LoadMoreCallback{
    List loadMoreSuccess(int page, List list, int totalCount);
    void loadMoreFail(int page, int totalCount);
}
