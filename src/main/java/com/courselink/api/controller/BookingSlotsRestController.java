package com.courselink.api.controller;

import com.courselink.api.dto.BookingSlotDTO;
import com.courselink.api.exception.ApiError;
import com.courselink.api.service.BookingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for managing booking slots in the system.
 * Provides endpoints for generating, choosing, deleting, and retrieving booking slots for defence sessions.
 */
@Tag(name = "Booking Slots Module", description = "APIs for managing booking slots in the system")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingSlotsRestController {

    private final BookingSlotService bookingSlotService;

    /**
     * Generates booking slots for a specific defence session.
     *
     * @param defenceSessionId The ID of the defence session for which to generate booking slots.
     * @param bookingSlotsCount The number of booking slots to generate.
     * @return A list of generated booking slots.
     */
    @Operation(
            summary = "Generate booking slots for a defence session",
            description = "Generates a specified number of booking slots for a given defence session.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking slots successfully generated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BookingSlotDTO.class))}),
            @ApiResponse(responseCode = "422", description = "Booking slots count must be greater than 0",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Defence session with current ID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Booking slots for current defence session ID already exist",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Booking slot should contain start time",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Booking slot should contain end time",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Booking slot should contain a defence session",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
    })
    @PostMapping("/booking-slots/generate-booking-slots")
    @ResponseStatus(HttpStatus.CREATED)
    public List<BookingSlotDTO> generateBookingSlots(
            @Parameter(description = "Defence session ID for generating booking slots") @RequestParam long defenceSessionId,
            @Parameter(description = "Number of booking slots to generate") @RequestParam int bookingSlotsCount
    ) {
        return bookingSlotService.generateBookingSlots(defenceSessionId, bookingSlotsCount);
    }

    /**
     * Allows a user to choose a booking slot.
     *
     * @param userId The ID of the user who is choosing a booking slot.
     * @param bookingSlotId The ID of the booking slot being chosen.
     * @return The chosen booking slot.
     */
    @Operation(
            summary = "Choose a booking slot",
            description = "Allows a user to choose a booking slot based on the provided user and slot IDs.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking slot successfully chosen",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BookingSlotDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User with specified ID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "404", description = "Booking slot with specified ID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "User with specified ID is not a student",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Booking slot with specified ID is already booked",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Booking slot should contain start time",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Booking slot should contain end time",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Booking slot should contain a defence session",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
    })
    @PutMapping("/booking-slots/choose-booking-slot")
    @ResponseStatus(HttpStatus.OK)
    public BookingSlotDTO chooseBookingSlot(
            @Parameter(description = "User ID for choosing a booking slot") @RequestParam long userId,
            @Parameter(description = "Booking slot ID to choose") @RequestParam long bookingSlotId
    ) {
        return bookingSlotService.chooseBookingSlot(userId, bookingSlotId);
    }

    /**
     * Removes booking slots for a specified defence session.
     *
     * @param defenceSessionId The ID of the defence session for which to remove booking slots.
     */
    @Operation(
            summary = "Remove booking slots by defence session ID",
            description = "Removes all booking slots associated with the specified defence session.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking slots successfully removed",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BookingSlotDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Booking slot for the specified defence session ID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
    })
    @DeleteMapping("/booking-slots/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeByDefenceSessionId(
            @Parameter(description = "Defence session ID to remove booking slots for") @RequestParam long defenceSessionId
    ) {
        bookingSlotService.removeBookingSlotByDefenceSessionId(defenceSessionId);
    }

    /**
     * Retrieves all booking slots for a specified defence session.
     *
     * @param defenceSessionId The ID of the defence session to retrieve booking slots for.
     * @return A list of booking slots for the specified defence session.
     */
    @Operation(
            summary = "Retrieve booking slots by defence session ID",
            description = "Retrieves all booking slots for the given defence session.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking slots successfully retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BookingSlotDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Booking slots for the specified defence session ID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))}),
    })
    @GetMapping("/booking-slots")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingSlotDTO> getAllByDefenceSessionId(
            @Parameter(description = "Defence session ID to retrieve booking slots for") @RequestParam long defenceSessionId
    ) {
        return bookingSlotService.getAllByDefenceSessionId(defenceSessionId);
    }

}
