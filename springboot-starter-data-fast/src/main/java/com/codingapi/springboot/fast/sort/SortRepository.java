package com.codingapi.springboot.fast.sort;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.ArrayList;
import java.util.List;

@NoRepositoryBean
public interface SortRepository<T extends ISort, ID> extends JpaRepository<T, ID> {


    default void pageSort(PageRequest request, List<ID> ids) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            ISort entity = getReferenceById(ids.get(i));
            entity.setSort(i + (request.getPageNumber() * request.getPageSize()));
            list.add((T) entity);
        }
        saveAll(list);
    }

}
