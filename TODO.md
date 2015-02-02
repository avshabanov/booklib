
## Registration

In Registration Controller:

```java
u.setUsername(username);
u.setPassword(passwordEncoder.encodePassword(
              plainTextPassword, saltSource.getSalt(u)));
```
