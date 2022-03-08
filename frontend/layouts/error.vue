<template>
  <div>
    <h1 v-if="error.statusCode === 404">Page not found</h1>
    <h1 v-else>An error occurred</h1>
    <pre>{{ JSON.stringify(error, null, '\t') }}</pre>
  </div>
</template>

<script>
export default {
  layout: 'default',
  props: ['error']
}
</script>
