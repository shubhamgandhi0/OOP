package com.minibank.repository;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.util.List;

public interface Repository<T> {
    void save(T entity);
    T findById(String id);
    List<T> findAll();
}
