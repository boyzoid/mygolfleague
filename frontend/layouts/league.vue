<template>
  <div>
    <nav class="navbar navbar-expand-md fixed-top bg-primary">
      <nuxt-link to="/" class="navbar-brand">My Golf League</nuxt-link>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExampleDefault" aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item">
            <nuxt-link to="/">Home</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/news">News</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/rules">Rules</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/stats">Stats</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/roster">Roster</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/schedule">Schedule</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/leaderboard">Leaderboard</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/account">My Account</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/contact">Contact us</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/admin">League Admin</nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link to="/logout">Logout</nuxt-link>
          </li>
        </ul>
      </div>
    </nav>

    <main role="main">

      <Nuxt />

    </main>

    <footer class="container">
      <p>&copy; Company 2017-2021</p>
    </footer>
  </div>
</template>
<script>
export default {
  name: 'League-layout'
}
</script>