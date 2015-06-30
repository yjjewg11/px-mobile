package com.company.common;

import java.util.*;

/**
 * <p>Title: 页面控制</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company:sobey </p>
 *
 * @author huxiao
 * @version 1.0
 */
public class PageCT {
  private int totalRows = 0;
  private int totalPages = 0;
  private int currentPage = 0;
  private int pageSize = 0;
  private int nextPage = 0;
  private int previousPage = 0;
  private boolean ispreviousPage = false;
  private boolean isnextPage = false;
  private int startpage = 0;
  private int endpage = 0;
  private List pageQueue = new ArrayList();

  public PageCT(int currentPage, int pageSize, int totalRows) {
    if (totalRows > 0) {
      this.totalRows = totalRows;
      if(totalRows%pageSize==0)
      this.totalPages = totalRows / pageSize;
      else
      this.totalPages = totalRows / pageSize + 1;
      this.currentPage = currentPage;
      this.pageSize = pageSize;
      this.nextPage = currentPage + 1;
      this.previousPage = currentPage - 1;
      if (currentPage > 1) {
        this.ispreviousPage = true;
      }
      if (currentPage < this.totalPages) {
        this.isnextPage = true;
      }
      this.startpage = 1;
      this.endpage = this.totalPages;

      if (this.totalPages > 1) {
        if (currentPage > 5) {
          for (int i = currentPage - 5; i < currentPage; i++) {
            pageQueue.add(Integer.toString(i));
          }
        }
        else {
          for (int i = 1; i < currentPage; i++) {
            pageQueue.add(Integer.toString(i));
          }
        }
        pageQueue.add(Integer.toString(currentPage));
        if ( (this.totalPages - currentPage) > 5) {
          for (int i = currentPage + 1; i <= currentPage + 5; i++) {
            pageQueue.add(Integer.toString(i));
          }
        }
        else {
          for (int i = currentPage + 1; i <= this.totalPages; i++) {
            pageQueue.add(Integer.toString(i));
          }
        }
      }
    }
  }

  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setNextPage(int nextPage) {
    this.nextPage = nextPage;
  }

  public void setPreviousPage(int previousPage) {
    this.previousPage = previousPage;
  }

  public void setIspreviousPage(boolean ispreviousPage) {
    this.ispreviousPage = ispreviousPage;
  }

  public void setIsnextPage(boolean isnextPage) {
    this.isnextPage = isnextPage;
  }

  public void setStartpage(int startpage) {
    this.startpage = startpage;
  }

  public void setEndpage(int endpage) {
    this.endpage = endpage;
  }

  public void setPageQueue(List pageQueue) {
    this.pageQueue = pageQueue;
  }

  public int getTotalRows() {
    return totalRows;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getPageSize() {
    return pageSize;
  }

  public int getNextPage() {
    return nextPage;
  }

  public int getPreviousPage() {
    return previousPage;
  }

  public boolean isIspreviousPage() {
    return ispreviousPage;
  }

  public boolean isIsnextPage() {
    return isnextPage;
  }

  public int getStartpage() {
    return startpage;
  }

  public int getEndpage() {
    return endpage;
  }

  public List getPageQueue() {
    return pageQueue;
  }

}
