package com.saga.opencms.util
/**
 * Created by jgregorio on 29/11/2017.
 */
class SgList {

    /**
     * Paginate list using sublist. Better to paginate list directly.
     * @param pageNum [0,]
     * @param pageSize [1,]
     * @param list
     * @return
     */
    public List paginate(int pageNum, int pageSize, List list) {
        // start min value allowed = 0
        // rows min value allowed = 1
        if (pageNum < 0 || pageSize < 1) {
            // return empty list
            return new ArrayList();
        }
        int paginationFrom = pageNum * pageSize;
        int paginationTo = paginationFrom + pageSize;
        int total = list.size();

        // paginationFrom max value allowed = total - 1
        if (paginationFrom >= total) {
            // return empty list
            return new ArrayList();
        }
        // paginationFrom max value allowed = total
        if (paginationTo > total) {
            // until last element
            paginationTo = total;
        }
        return list.subList(paginationFrom, paginationTo);
    }

}