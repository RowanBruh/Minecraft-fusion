const express = require('express');
const path = require('path');
const app = express();
const PORT = process.env.PORT || 5000;

// Serve static files from the parent directory
app.use(express.static(path.join(__dirname, '..')));

// Route for the homepage
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, '..', 'index.html'));
});

// Start the server
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
    console.log(`Visit http://localhost:${PORT} to view the website`);
});