
# 💸 CashFlow – SMS-Based Personal Finance Tracker

CashFlow is a smart, lightweight Android app that automatically scans your SMS inbox to extract and track debit/credit transactions across banks like BOI, Federal Bank, HDFC, ICICI, SBI, and Axis. It helps you manage your expenses with monthly summaries, merchant breakdowns, and export options — all without needing internet or login.

---

## ✨ Features

- 📥 **Auto SMS Parsing**: Detects UPI, IMPS, and bank alerts for debit and credit transactions.
- 📆 **Monthly Grouping**: View summaries month-wise with total debits and credits.
- 🏦 **Bank-Aware Parsing**: Supports custom formats from BOI, Federal, SBI, HDFC, ICICI, and more.
- 📊 **Insightful Dashboard**: Summary cards for balance, total expenses, and income.
- 🔍 **Manual Scan**: Scan past SMSes on demand without duplicating previous entries.
- 🧾 **Transaction Details**: Shows merchant, date, amount, mode, and bank source.
- 🔄 **Offline First**: Works entirely offline. No data leaves your device.
- 📤 **Export Support** *(coming soon)*: Export your transactions to CSV or sync with cloud.

---

## 📸 Screenshots

| Home Screen | Transaction Details | Monthly Summary |
|-------------|---------------------|-----------------|
| *(Add images here)* | *(Add images here)* | *(Add images here)* |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- minSdk: 23  
- targetSdk: 34+

### Permissions

Make sure you request and handle the following permissions:

```xml
<uses-permission android:name="android.permission.READ_SMS"/>
<uses-permission android:name="android.permission.RECEIVE_SMS"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
```

---

## 📂 Project Structure

```
com.personal.cashflow/
├── activities/          # Main screens and UI
├── room/                # Room database (TransactionModel, DAO)
├── utils/               # SMS Parser, MonthlyGrouper, DateUtils
├── adapter/             # RecyclerView Adapters
├── receiver/            # SMS BroadcastReceiver
└── repository/          # TransactionRepository (Singleton)
```

---

## 🧠 Parsing Logic

CashFlow uses regex-based parsers to extract details like:
- **Amount**
- **Type** (credit / debit)
- **Merchant**
- **Mode** (UPI, IMPS)
- **Date & Time**
- **Bank Source** (based on SMS suffix)

See: [`SmsParser.java`](app/src/main/java/com/personal/cashflow/utils/SmsParser.java)

---

## 🛠 Development TODOs

- [ ] Cloud sync (Firebase or Google Drive)
- [ ] Backup & restore support
- [ ] Transaction editing UI
- [ ] Dark mode toggle
- [ ] Category tagging & filters

---

## 📜 License

MIT License. Free for personal and commercial use.

---

## 👨‍💻 Developed By

**Ashish Yadav**  
Android Developer | Open Source Contributor  
📧 ya0285981@gmail.com  
📱 [LinkedIn](https://linkedin.com/in/ashish8381))

---

> 💬 _“Take control of your money — one SMS at a time.”_
