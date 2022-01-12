package com.example.socialnetworkgui.repository.paging;

import com.example.socialnetworkgui.domain.Entity;
import com.example.socialnetworkgui.repository.Repository;

public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}
