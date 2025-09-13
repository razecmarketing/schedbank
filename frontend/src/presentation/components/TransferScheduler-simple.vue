<template>
  <div>
    <h2>Schedule Transfer</h2>
    <form @submit.prevent="scheduleTransfer">
      <div>
        <label>Source Account:</label>
        <input v-model="form.sourceAccount" placeholder="1234567890" />
      </div>
      <div>
        <label>Target Account:</label>
        <input v-model="form.targetAccount" placeholder="0987654321" />
      </div>
      <div>
        <label>Amount:</label>
        <input v-model="form.amount" type="number" step="0.01" />
      </div>
      <div>
        <label>Transfer Date:</label>
        <input v-model="form.transferDate" type="date" />
      </div>
      <button type="submit">Schedule Transfer</button>
    </form>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'TransferScheduler',
  setup() {
    const form = ref({
      sourceAccount: '',
      targetAccount: '',
      amount: '',
      transferDate: ''
    })

    const scheduleTransfer = () => {
      console.log('Scheduling transfer:', form.value)
      alert('Transfer scheduled successfully!')
    }

    return {
      form,
      scheduleTransfer
    }
  }
}
</script>
