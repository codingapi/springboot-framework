# Domain-Driven Design (DDD)

2026-08-18 lorne

[中文](./DDD_CN.md) | English

**DDD lets the business define the model, the model organize the software, and data and technology serve the business.**

## What Is It?

Domain-Driven Design is a methodology that starts from the business and uses the domain model as the core around which software design is organized. It abstracts business problems into a domain model, uses the model to analyze the business, express rules and solve problems, and thereby guides how business, data and technology are organized.

DDD provides a fundamental direction and criteria of judgment for software design, not a fixed development method. It does not mandate any particular architecture, design pattern, technical framework or implementation process. Whether a design conforms to DDD is not determined by which forms are used, but by whether the business is accurately expressed, whether the domain model genuinely carries business responsibilities, and whether data and technology are organized around the business to provide support.

In the practice described in this article, the domain model is expressed through object-oriented design, and appropriate architectural design is used to decouple business, data and technology. This is not done to comply with some DDD specification, but to protect the domain model — so that data stays data, technology stays technology, and business stays business, each fulfilling its own role and collaborating with low coupling.

Take "cancel order" as an example: whether an order can be cancelled, how its state changes after cancellation, and whether a refund is required are business rules and should be decided by the order domain model; how orders are queried and persisted is a data concern and should be handled by the data layer; how the payment platform is invoked to complete a refund is a technical concern and should be handled by the infrastructure.

Therefore, DDD is not simply wrapping database tables as classes, but letting the domain model genuinely carry business responsibilities. An order is no longer just an object that stores data, but a business object that can decide whether cancellation is allowed, execute state transitions, and produce the corresponding business outcomes.

## What Does It Solve?

### 1. Business Complexity Becomes Manageable

Domain-Driven Design decouples business, data and technology, reduces the dependencies among them, and confines complexity within clear boundaries. Through object-oriented design, the business is abstracted and modeled so that the domain model gains better expressiveness and adaptability, making it easier to cope with complex and continuously changing business.

For example, an order may have rules such as "unpaid orders can be cancelled directly", "paid orders require a refund when cancelled", and "shipped orders cannot be cancelled". If these decisions are scattered across APIs, database scripts and process code, the more rules there are, the harder the system becomes to understand and modify. DDD centralizes these rules in the order model, so developers can handle changes directly around the order's states and behaviors.

DDD does not eliminate the complexity of the business itself; rather, it concentrates complexity that was previously scattered and hidden into the business model, where it can be explicitly expressed, understood and controlled.

### 2. Software Quality Becomes Easier to Guarantee

The prerequisite for continuously coping with change is a solid automated regression testing mechanism. Domain-Driven Design decouples business, data and technology so that each part can be tested independently and explicitly around its own responsibility, improving the testability of the whole system.

Tests of the domain model focus on business rules: they only need to construct business objects and verify their behavior, with no dependency on databases or external systems. Once the data layer is stripped of business logic, it can focus on testing storage, querying, mapping and transactions. The technology layer can focus on testing API calls, message passing and external system integration.

For example, when testing "cancel order", the domain model verifies whether cancellation is allowed in each state; the data layer verifies that the order state after cancellation can be persisted correctly; the technology layer verifies that the refund request can be sent to the payment platform correctly.

This design not only makes business rules easier to test, but also frees data and technology from complex business scenarios, turning them into clearly scoped functional tests. Each part can be verified around its own responsibility, making problems easier to find and locate, and together they form a complete automated regression testing system that safeguards quality as the software continuously evolves.

### 3. Business Capabilities Can Be Accumulated and Reused

As Domain-Driven Design is continuously practiced, what gets accumulated are domain models containing business rules and behaviors, which can further evolve into independent business components. Because domain models do not directly depend on concrete data structures or technical frameworks, they are easier to reuse and continuously evolve in systems with the same business semantics.

For example, an order model refined over a long period may already include capabilities such as amount calculation, state transitions, cancellation, refund and fulfillment. When another system needs the same order capabilities, it can reuse these proven models and components, and then implement the corresponding technical adaptations for its own database, payment platform and messaging system.

Note that the premise of reuse is that the two systems define the business in essentially the same way. What DDD reuses first is business knowledge, models and rules; only when business semantics are consistent does it proceed to reuse concrete code.

## What Is It Not?

### 1. DDD Is Not a Technical Framework

DDD is not some development framework, class library or fixed code structure, but a way of thinking about organizing software with the business at the core.

Technical frameworks solve how a program runs; DDD solves how the business is understood, modeled and implemented. Frameworks can be replaced and databases can be swapped, but the business rules expressed by the domain model should remain relatively stable.

Therefore, using a certain layered framework does not mean using DDD, and not using a specific framework does not mean DDD cannot be practiced.

### 2. DDD Is Not a Four-Layer Architecture

DDD often adopts a layered architecture of user interface, application, domain and infrastructure, but DDD itself is not equivalent to a four-layer architecture.

The role of a four-layer architecture is to divide system responsibilities and isolate technical details: the user interface layer receives requests, the application layer orchestrates business processes, the domain layer implements business rules, and the infrastructure layer provides technical capabilities such as databases, messaging and external systems. It is an architectural means of protecting the domain model, not the definition of DDD.

DDD can also be realized through hexagonal architecture, clean architecture, or other architectural forms. Which architecture is adopted is not what matters; what matters is whether the business is expressed by the domain model, and whether data and technology are kept outside clear boundaries.

Therefore, a project adopting a four-layer architecture does not mean it has implemented DDD; a project not adopting the standard four-layer structure does not mean it is not DDD either.

### 3. DDD Is Not a Set of Specifications and Standards

DDD is not a set of rules that must be strictly followed, nor is there a single correct implementation.

Entities, value objects, aggregates, repositories, domain services, four-layer architecture and hexagonal architecture are all optional tools that help developers establish or protect the domain model, not standard answers for judging whether a project adopts DDD.

If a simpler design can already express the business accurately, there is no need to introduce more concepts for the sake of formal completeness; if the current design can no longer carry the growing business complexity, more suitable modeling and architectural means should be introduced.

Whether DDD is practiced should not be judged by how much DDD terminology a project uses, but by whether the software is genuinely centered on the business model.

### 4. DDD Is Not Exclusive to Complex Systems

Many people believe that simple CRUD applications are not suitable for DDD because it adds extra design and development cost. I do not fully agree with this view.

Regardless of project size, one must face the problems of code coupling, quality assurance and continuous maintenance. A small project may only have simple CRUD today, but as business rules keep growing, if the code is always organized around the database, it will gradually become hard to understand and modify as well.

Mastering DDD does not mean using exactly the same design in every project. Simple projects can adopt lightweight domain models and layering, while complex projects need more explicit business boundaries and more complete modeling methods.

Project size determines the depth at which DDD is applied, not whether business modeling, separation of responsibilities and quality assurance are needed.

### 5. DDD Is Not an Accumulation of Design Patterns

Entities, value objects, aggregates, repositories and domain services are all tools for implementing the domain model, but using these concepts does not mean truly practicing DDD.

If classes and interfaces are added mechanically while the model fails to express the business accurately, such designs only add formality and complexity to the system.

The point of DDD is not how many design patterns are used, but whether business rules are expressed clearly, accurately and cohesively. Use what is needed; designs that are not needed should not be forced in for the sake of formal completeness.

### 6. DDD Is Not Converting Database Tables into Classes

Mapping one database table to one entity class is essentially still data modeling, not domain modeling.

Data objects express how data is stored; domain objects express how the business operates. A domain object contains not only data, but also business rules, state transitions and behavioral constraints.

DDD is not writing business code around database tables, but first establishing the business model and then deciding how to persist the data the model produces.

### 7. DDD Is Not a One-Time, Never-Changing Design

The domain model is not a static artifact designed once at the beginning of a project. As the team's understanding of the business deepens and the business itself keeps changing, the domain model also needs continuous adjustment and evolution.

Therefore, DDD is not about pursuing a perfect model from the start, but about continuously discovering the business, validating the model and correcting its expression during development, gradually bringing the software structure closer to the real business.

## In the Era of Vibe Coding, Where Is the Value of DDD?

Vibe Coding solves the problem of rapid code generation, but to be truly used in complex systems, it must also solve how generated results are verified, how existing capabilities are reused, and how the system continuously evolves.

The core value DDD provides in the era of Vibe Coding is precisely making Vibe Coding testable, reusable and sustainable.

### 1. Testable: Establishing a Self-Verification Mechanism

Whether the code generated by Vibe Coding is correct cannot rely solely on manual reading or on whether the functionality runs; it must be verified through automated tests.

DDD centralizes business rules in independent domain models, allowing AI to generate and execute tests against business behavior and continuously correct the code based on test results. Tests thus become the self-verification mechanism of Vibe Coding, establishing a stable quality feedback loop for code generation.

### 2. Reusable: Improving Both Efficiency and Quality

If every development session lets AI regenerate the same business logic from scratch, it not only wastes efficiency but also easily produces divergent implementations and new errors.

DDD accumulates business rules into domain models and business components. Models verified by tests can be reused, enabling AI to compose and extend based on existing capabilities. Reuse reduces repeated development and repeated mistakes, thus improving both development efficiency and software quality.

### 3. Sustainable: Supporting Complex System Development

Complex systems are not generated in one shot, but evolve gradually amid continuous change.

Through stable domain models, clear business boundaries, and the separation of business from technology, DDD controls the chaos and complexity brought by continuous code generation. It enables AI to understand, modify and extend the system within explicit boundaries, preventing local changes from constantly affecting the whole.

Therefore, sustainability is the key for Vibe Coding to enter complex system development. Without sustainable models and boundaries, Vibe Coding can only quickly complete isolated features; only with sustainable capabilities can it participate in long-term, complex software construction.

## Conclusion

DDD is not a fixed architecture, specification or development process, but a methodology that starts from the business and organizes software design around the domain model.

DDD itself does not prescribe concrete implementation approaches. In engineering practice, the domain model can be realized through object-oriented design, business, data and technology can be decoupled through appropriate architectural design, and each responsibility can be verified separately through automated tests, so that the business model can be understood, verified, reused and continuously evolved.

In the era of Vibe Coding, generating code will become ever easier, but judging whether code is correct, avoiding repeated generation, and supporting the long-term evolution of complex systems will become ever more important. Code can be generated quickly, but business models that are verified, reusable and capable of continuous evolution are the true core assets that software can accumulate over the long term.
