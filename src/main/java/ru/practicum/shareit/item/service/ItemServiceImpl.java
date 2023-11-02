package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repisitory.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
	private ItemRepository itemRepository;
	private UserRepository userRepository;
	private BookingRepository bookingRepository;
	private CommentRepository commentRepository;

	public ItemDto create(Long userId, ItemDto itemDto) {
		Item item = ItemMapper.toItem(itemDto);
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			if (itemDto.getAvailable() == null || itemDto.getName() == null || itemDto.getDescription() == null ||
					itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
				throw new UserDoesNotExistException("Имя, опоисание и статус доступа должны быть заполнены");
			}
			throw new UserNotFoundException("Пользователя с id: " + userId + " не существует");
		}
		User user = userOptional.get();
		item.setOwner(user);
		itemRepository.save(item);
		itemDto = ItemMapper.toItemDto(item, null,
				null, null);
		return itemDto;
	}

	public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
		Optional<Item> itemOptional = itemRepository.findById(itemId);
		if (itemOptional.isEmpty()) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
		Item oldItem = itemOptional.get();
		User owner = oldItem.getOwner();
		if (!Objects.equals(owner.getId(), userId)) {
			throw new ItemOwnerException("Пользователь с id=" + userId + " не владеет вещью с id=" + itemId);
		}
		Item updateItem = ItemMapper.toItem(itemDto, owner);
		String name = updateItem.getName();
		String description = updateItem.getDescription();
		if (name == null || name.isBlank()) updateItem.setName(oldItem.getName());
		if (description == null || description.isBlank()) updateItem.setDescription(oldItem.getDescription());
		if (updateItem.getAvailable() == null) updateItem.setAvailable(oldItem.getAvailable());
		updateItem.setId(itemId);
		Item item = itemRepository.save(updateItem);
		return ItemMapper.toItemDto(item, null);
	}

	public ItemDto getItemById(Long userId, Long itemId) {
		Optional<Item> itemOptional = itemRepository.findById(itemId);
		if (itemOptional.isEmpty()) throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
		Item item = itemOptional.get();
		Booking last = null;
		Booking next = null;
		if (Objects.equals(item.getOwner().getId(), userId)) {
			LocalDateTime now = LocalDateTime.now();
			last = bookingRepository.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
					itemId,
					Status.REJECTED,
					now
			);
			next = bookingRepository.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
					itemId,
					Status.REJECTED,
					now
			);
		}
		List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
				.map(comment -> CommentMapper.commentToDto(comment, comment.getAuthor().getName()))
				.collect(Collectors.toList());
		BookingDto lastBookingDto = last != null ? BookingMapper.toBookingDto(last) : null;
		BookingDto nextBookingDto = next != null ? BookingMapper.toBookingDto(next) : null;
		return ItemMapper.toItemDto(item, lastBookingDto, nextBookingDto, comments);
	}

	public Collection<ItemDto> getItemsUser(Long userId) {


		return itemRepository.findByOwnerId(userId).stream()
				.map(item -> {
					Booking last = null;
					Booking next = null;
					if (Objects.equals(item.getOwner().getId(), userId)) {
						LocalDateTime now = LocalDateTime.now();
						last = bookingRepository.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
								item.getId(),
								Status.REJECTED,
								now
						);
						next = bookingRepository.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
								item.getId(),
								Status.REJECTED,
								now
						);
					}
					List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId())
							.stream()
							.map(comment -> CommentMapper.commentToDto(comment, comment.getAuthor().getName()))
							.collect(Collectors.toList());
					BookingDto lastBookingDto = last != null ? BookingMapper.toBookingDto(last) : null;
					BookingDto nextBookingDto = next != null ? BookingMapper.toBookingDto(next) : null;
					return ItemMapper.toItemDto(item, lastBookingDto, nextBookingDto, comments);
				})
				.collect(Collectors.toList()
				);
	}

	public Collection<ItemDto> getItemsByNameOrDescription(Long userId, String text) {
		if (text.isBlank()) {
			return new ArrayList<>();
		}
		return itemRepository.getItemsByNameOrDescription(text).stream()
				.map(item -> ItemMapper.toItemDto(item,
						commentRepository.findByItemIdOrderByCreatedDesc(item.getId()).stream()
								.map(comment -> CommentMapper.commentToDto(comment, comment.getAuthor().getName()))
								.collect(Collectors.toList())))
				.collect(Collectors.toList());
	}

	public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
		if (commentDto.getText().isBlank()) {
			throw new CommentaryEmptyException("Комментарий не может быть пустым");
		}
		Optional<Item> itemOptional = itemRepository.findById(itemId);
		if (itemOptional.isEmpty()) {
			throw new ItemNotFoundException("Вещь с id: " + itemId + " не найдена");
		}
		Item item = itemOptional.get();
		Optional<User> authorOptional = userRepository.findById(userId);
		if (authorOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id: " + userId + " не найден");
		}
		User author = authorOptional.get();
		if (bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()).size() == 0) {
			throw new BookingTimeException("Отзыв возможно написать только после бранирования вещи");
		}
		Comment comment = CommentMapper.toComment(commentDto, item, author);
		comment.setCreated(LocalDateTime.now());
		commentRepository.save(comment);
		return CommentMapper.commentToDto(comment, author.getName());
	}
}
