# Thoughts and Assumptions

This document captures the reasoning behind the solution, the assumptions I made while interpreting the assignment and the trade-offs I considered.

## What I Optimized For

I approached the task as a small product feature rather than only an algorithm exercise.

The goal was to deliver:

- a clear and usable customer flow
- a simple and readable backend implementation
- predictable API behavior
- enough structure to show how the solution could evolve beyond the assignment

## Key Assumptions

The assignment leaves a few details open to interpretation, so I made the following assumptions:

1. The provided personal codes are the only supported applicants in this mocked version.
2. Unsupported personal codes should be treated as invalid input for this exercise.
3. Debt is an immediate hard rejection, regardless of requested amount or period.
4. When a valid offer exists, the engine should return the maximum amount available for the selected period.
5. If the selected period does not produce a valid offer, the engine should try longer periods until a valid offer is found or the maximum period is reached.
6. The requested amount helps define the user's intent, but the final response should still return the best offer the engine can approve according to the assignment wording.

If this were a real product task, I would confirm these assumptions with the product owner before implementation.

## Why I Structured the Backend This Way

I kept the backend split into a few small parts:

- `DecisionController` for transport concerns
- `DecisionService` for business rules
- DTOs for request and response contracts
- a global exception handler for validation errors


## Backend Thought Process

For the decision logic itself, I followed the assignment wording as closely as possible and tried to keep the algorithm explicit rather than clever.

My reasoning was:

- validate the input first
- determine the applicant segment
- reject immediately if the applicant has debt
- try to calculate the best offer for the selected period
- if no valid offer exists for that period, try longer periods
- return the first valid best offer found

## Why I Structured the Frontend This Way

I kept the frontend intentionally focused on one user journey:

- enter personal code
- choose amount
- choose period
- instantly see the best available result

I used a single-page form with immediate recalculation once the personal code is valid. From a UX standpoint, this keeps the interaction lightweight and helps the reviewer quickly understand the system behavior.

I also chose to show distinct outcomes:

- debt state
- validation or connectivity problems
- successful offer
- no valid loan result

That separation matters because these outcomes have very different product meanings.

## Frontend UI Thought Process

For the frontend, I did not want to create a generic developer-looking form. Since this is an Inbank-related assignment, I wanted the interface to feel closer to a financial product rather than a raw demo page.

Because of that, I took visual inspiration from Inbank’s website, especially in these areas:

- soft card-based layout
- clean spacing
- simple, focused input flow
- minimal distractions
- calm color palette
- emphasis on the result area

My goal was to make the UI feel like it belongs in the same product family while still keeping the implementation small and manageable for the assignment.

I also intentionally kept the layout quite compact. Since the app only solves one task, I felt it was better to keep the experience narrow and focused instead of spreading content across multiple sections or pages.

## Why I Chose Instant Feedback

One product decision I made was to calculate the offer automatically instead of requiring the user to press a separate submit button.

I chose that approach because:

- it makes the tool feel faster
- it reduces unnecessary interaction
- it helps demonstrate the backend behavior immediately
- it fits well for a calculator-style interface

To avoid sending requests too aggressively while the user is typing, I added a small delay before the request is sent. This keeps the UI responsive while still avoiding a noisy API call on every keystroke.

## Trade-Offs and Limitations

There are a few conscious shortcuts because this is a take-home task:

- Personal code segmentation is hardcoded instead of coming from an external service.
- The algorithm is intentionally simple and follows the assignment exactly, even though real lending logic would be much richer.
- There is limited domain modeling because the main goal here is readability and task fit, not enterprise complexity.
- The frontend is optimized for demonstration and usability rather than a full production design system.


## What I Would Improve Next

If I had more time, I would improve the solution in this order:

1. Add backend tests around the decision engine, especially for edge cases near the minimum and maximum amount and period limits.
2. Add contract-level tests for API error responses so the frontend and backend stay aligned.
3. Improve the frontend result presentation by explaining why a period or amount changed from the user's request.

## One Improvement to the Assignment

If I were to pick one primary improvement, it would be to standardize the test data through logic rather than static values.

Currently, the assignment relies on four specific hardcoded personal codes. I would improve this by defining the credit segments based on a property of the personal code itself (for example, using the last digit to determine the modifier).

Why this improvement?
1. Scalability: It allows the reviewer to test a much wider range of edge cases without manually copy-pasting four specific strings.
2. Realism: It better mimics a real-world integration where a system must dynamically categorize a user profile based on incoming data.
3. Testing: It makes writing automated unit tests much more robust, as you can generate random valid codes for each segment.

## What I Would Ask in a Real Product Discussion

If this were a real product feature, I would want answers to these questions before building the next iteration:

- Should unsupported personal codes be rejected, or should they map to a default segment?
- If the customer asks for a smaller amount than the maximum possible offer, should we still return the larger amount or respect the requested amount?
- If multiple periods are valid, should we optimize for maximum approved amount, shortest period, lowest monthly burden, or closest match to the customer request?
- Should the API return only the best offer, or a list of valid offers for the frontend to present?
- How should the frontend explain a negative decision in a way that is clear but still compliant with legal and business requirements?

