# Fee Calculation Module

A module that calculates transfer fees for a digital banking system.

## How to Run

**1. Clone the repository**
```bash
git clone <https://github.com/maryam370/Fee-Calculation-Module.git>
cd Fee-Calculation-Module
```

**2. Run the application**
```bash
mvn spring-boot:run
```

The app will start on `http://localhost:8080`

---

## Usage

Open `http://localhost:8080` in your browser to use the fee calculator UI.

### API Endpoints

**Calculate Fee**
```
POST /api/transfers/calculate-fee
```
```json
{
  "transactionAmount": 100.000,
  "transactionType": "P2P"
}
```

Transaction types: `P2P`, `ME2ME_SAME_CURRENCY`, `ME2ME_CROSS_CURRENCY`

---

**Get Current Fee Configuration**
```
GET /api/transfers/fee-config
```
