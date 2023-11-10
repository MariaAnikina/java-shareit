package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
	List<Item> findByOwnerId(Long userId, Pageable pageRequest);

	@Query("select it from Item as it where (lower(it.name)) like lower(concat('%', ?1,'%')) " +
			"or (lower(it.description)) like lower(concat('%', ?1,'%')) and it.available = TRUE")
	Collection<Item> getItemsByNameOrDescription(String text);

	Collection<Item> findByRequestId(Long id);
}
