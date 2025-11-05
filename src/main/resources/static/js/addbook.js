const addBookIcon = document.getElementById("addBookIcon");
const addBookModal = document.getElementById("addBookModal");
const closeModal = document.getElementById("closeModal");

addBookIcon.addEventListener("click", () => {
    addBookModal.style.display = "flex";
});

closeModal.addEventListener("click", () => {
    addBookModal.style.display = "none";
});

document.getElementById("addBookForm").addEventListener("submit", (e) => {
    e.preventDefault();

    // Lấy giá trị từ các trường trong form
    const bookId = document.getElementById("bookId").value;
    const bookTitle = document.getElementById("bookTitle").value;
    const bookGenres = document.getElementById("bookGenres").value;

    // Tạo đối tượng book dưới dạng JSON
    const bookData = {
        id: bookId,
        title: bookTitle,
        genres: bookGenres
    };

    console.log("Sending book data:", bookData); // Debugging

    // Gửi yêu cầu POST đến server
    fetch("/books/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(bookData)
    })
        .then((response) => {
            if (response.redirected) {
                // Nếu server redirect, chuyển hướng trình duyệt
                window.location.href = response.url;
            } else if (response.ok) {
                return response.text();
            } else {
                throw new Error("Failed to add book");
            }
        })
        .then((data) => {
            console.log("Success:", data);
            alert("Book added successfully!");
            e.target.reset();
            addBookModal.style.display = "none";
        })
        .catch((error) => {
            console.error("Error:", error);
            alert("Failed to add book!");
        });
});
