<template>
  <div v-if="hasErrors" class="alert alert-danger" role="alert">
    <div v-for="(error, index) in errors" :key="index">{{ error }}</div>
  </div>
</template>

<script>
export default {
  name: "FormErrors",
  props: {
    errors: {
      type: Array,
      required: true,
    },
  },
  computed: {
    hasErrors() {
      return this.errors && this.errors.length > 0;
    },
  },
};
</script>
