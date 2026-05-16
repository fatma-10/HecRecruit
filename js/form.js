export function handleSubmit(event) {
  event.preventDefault();
  
  if (!validatePasswords()) return;

  const formData = collectFormData();
  submitForm(formData);
}

function validatePasswords() {
  const password = document.getElementById('password').value;
  const confirmPassword = document.getElementById('confirmPassword').value;
  
  if (password !== confirmPassword) {
    alert('Passwords do not match!');
    return false;
  }
  return true;
}

function collectFormData() {
  return {
    firstName: document.getElementById('firstName').value,
    lastName: document.getElementById('lastName').value,
    email: document.getElementById('email').value,
    phone: document.getElementById('phone').value,
    password: document.getElementById('password').value,
    address: document.getElementById('address').value,
    bio: document.getElementById('bio').value,
    newsletter: document.getElementById('newsletter').checked,
    terms: document.getElementById('terms').checked
  };
}

function submitForm(formData) {
  console.log('Form submitted:', formData);
  alert('Account created successfully!');
  document.getElementById('signupForm').reset();
}