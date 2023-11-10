package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
	List<ItemRequest> findByRequestorId(Long userId);

	List<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(Long userId, Pageable pageRequest);
}
