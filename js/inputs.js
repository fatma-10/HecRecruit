let delegationsData = {};

fetch("../data/tunisia-gov.json")
  .then((response) => response.json())
  .then((data) => {
    delegationsData = data;
    const regionSelect = document.getElementById("regionSelect");
    const regionArriveeSelect = document.getElementById("regionArriveeSelect");

    const regions = Object.keys(data);
    regions.forEach((region) => {
      const departOption = document.createElement("option");
      departOption.value = region;
      departOption.textContent = region;
      regionSelect.appendChild(departOption);

      const arriveeOption = document.createElement("option");
      arriveeOption.value = region;
      arriveeOption.textContent = region;
      regionArriveeSelect.appendChild(arriveeOption);
    });

    regionSelect.addEventListener("change", function () {
      const departDelegation = document.getElementById("departDelegation");
      departDelegation.innerHTML =
        "<option selected>Sélectionnez votre délégation</option>";

      if (this.value !== "Sélectionnez votre région") {
        const uniqueDelegations = [
          ...new Set(data[this.value].map((item) => item.delegation)),
        ];

        uniqueDelegations.forEach((delegation) => {
          const option = document.createElement("option");
          option.value = delegation;
          option.textContent = delegation;
          departDelegation.appendChild(option);
        });

        departDelegation.disabled = false;
      } else {
        departDelegation.disabled = true;
      }
    });

    regionArriveeSelect.addEventListener("change", function () {
      const arriveeDelegation = document.getElementById("arriveeDelegation");
      arriveeDelegation.innerHTML =
        "<option selected>Sélectionnez votre délégation</option>";

      if (this.value !== "Sélectionnez votre région") {
        const uniqueDelegations = [
          ...new Set(data[this.value].map((item) => item.delegation)),
        ];

        uniqueDelegations.forEach((delegation) => {
          const option = document.createElement("option");
          option.value = delegation;
          option.textContent = delegation;
          arriveeDelegation.appendChild(option);
        });

        arriveeDelegation.disabled = false;
      } else {
        arriveeDelegation.disabled = true;
      }
    });
  })
  .catch((error) => console.error("Error loading delegations:", error));
