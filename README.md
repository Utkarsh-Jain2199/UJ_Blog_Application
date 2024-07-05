# Project: Blog Application

## Part 1: CRUD
### Technologies Used:
- Spring MVC
- Spring Data JPA
- Thymeleaf

### Use Cases for Non-Logged-In Users:
- Read a list of blog posts with title, excerpt, author, published DateTime, and tags.
- Read full blog posts with title, content, author, published DateTime, and tags.
- Filter blog posts by author, published DateTime, and tags.
- Sort blog posts by published DateTime.
- Search blog posts using full-text search on title, content, author, and tags.
- Navigate through paginated blog lists (each page with a maximum of 10 posts).
- Create, update, and delete blog posts.
- Comment on blog posts using a form containing name, email, and comment.
- Read, update, and delete comments.

### Database Schema:
#### User
- id
- name
- email
- password

#### Posts
- id
- title
- excerpt
- content
- author
- published_at
- is_published
- created_at
- updated_at

#### Tags
- id
- name
- created_at
- updated_at

#### Post_Tags
- post_id
- tag_id
- created_at
- updated_at

#### Comments
- id
- name
- email
- comment
- post_id
- created_at
- updated_at

### Steps to Build:
1. Create HTML and CSS.
2. Design and implement the database schema.
3. Integrate Thymeleaf with HTML/CSS.
4. Connect data with the frontend using Spring MVC and Spring Data JPA.

### Steps for M2:
- Implement functionality for authors and tags.
- Enable commenting.
- Implement pagination, filters, sorting, and searching.

### Steps for M3 (Future Development):
- Authentication
- Authorization

---

## Part 2: Authentication & Authorization
### Technologies Used:
- Spring Boot
- Spring Data JPA
- Spring Security
- Thymeleaf

### Use Cases (Non-Logged-In & Logged-In Users):
- Read, filter, sort, and search blog posts.
- Create, update, and delete blog posts (based on roles).
- Comment on blog posts and manage own comments.
- Admin privileges for creating, updating, and deleting posts.

### Steps to Implement:
1. Incorporate Spring Security for authentication and authorization.
2. Enable user roles for different permissions.
3. Implement CRUD operations based on user roles.

