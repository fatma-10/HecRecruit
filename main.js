import './style.css';
import { initMap } from './js/maps.js';
import { handleSubmit } from './js/form.js';

// Attach form submit handler
document.getElementById('signupForm').addEventListener('submit', handleSubmit);

// Initialize the map when the page loads
window.onload = initMap;