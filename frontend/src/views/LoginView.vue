<template>
    <div class="login-view">
        <h1>Login</h1>
        <form>
            <input type="text" placeholder="Username" v-model="username" />
            <input type="password" placeholder="Password" v-model="password" />
            <button type="submit" @click.prevent="login">Login</button>
        </form>
    </div>
</template>

<script setup>
import { ref } from 'vue'

const username = ref('')
const password = ref('')

function login() {
    
}
</script>

<style scoped>
.login-view {
    max-width: 400px;
    margin: 40px auto;
    padding: 24px;
    border: 1px solid #ddd;
    border-radius: 8px;
}
form {
    display: flex;
    flex-direction: column;
    gap: 12px;
}
button {
    padding: 8px 0;
}
</style>