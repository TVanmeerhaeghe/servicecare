<template>
  <div class="modal-backdrop">
    <div class="modal-card">
      <header class="modal-header">
        <slot name="title">Confirmation</slot>
      </header>
      <div class="modal-body">
        <slot />
      </div>
      <footer class="modal-footer">
        <button class="btn btn-ghost" @click="$emit('cancel')">Annuler</button>
        <button class="btn btn-primary" @click="$emit('confirm')">Confirmer</button>
      </footer>
    </div>
  </div>
</template>