package com.codingapi.springboot.fast.sort;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface SortRepository<T extends ISort, I> extends JpaRepository<T, I> {


    default void sort(PageRequest request, List<Integer> ids) {
        for (int i = 0; i < ids.size(); i++) {
            ISort entity = getById((I) ids.get(i));
            entity.setSort(i + ((request.getPageNumber() - 1) * request.getPageSize()));
            save((T) entity);
        }
    }

}
