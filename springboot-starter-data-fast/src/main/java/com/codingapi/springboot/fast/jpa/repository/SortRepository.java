package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.framework.domain.ISort;
import com.codingapi.springboot.framework.dto.request.SortRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.ArrayList;
import java.util.List;

@NoRepositoryBean
public interface SortRepository<T extends ISort, ID> extends JpaRepository<T, ID> {


    default void reSort(SortRequest request) {
        if (request != null && !request.getIds().isEmpty()) {
            List<T> list = new ArrayList<>();
            int minSort = Integer.MAX_VALUE;
            for (Object objectId : request.getIds()) {
                ID id = (ID) objectId;
                T t = getReferenceById(id);
                if (t.getSort() != null && t.getSort() < minSort) {
                    minSort = t.getSort();
                }else{
                    minSort = 0;
                }
                list.add(t);
            }
            for (T t : list) {
                t.setSort(minSort++);
            }
            saveAll(list);
        }
    }


}
