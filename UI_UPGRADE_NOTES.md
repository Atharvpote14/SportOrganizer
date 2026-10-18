# Sport Organizer — Premium UI pass

This update refreshes the visual system while preserving the existing Supabase authentication, repositories, routes, and Platform Super Admin flow.

## Included
- Refined indigo/cyan palette and cleaner typography.
- New dashboard hero panel, status card, metric tiles, and action rows.
- Staggered fade/slide reveal on dashboard and sign-in screen.
- Branded animated session-loading screen.
- More polished sign-in form, field shapes, and primary button.
- Updated top bar and bottom navigation surface styling.

## Open and run
1. Extract the ZIP.
2. Open the `SportOrganizer` folder in Android Studio.
3. Restore your own `local.properties` from your private backup (do not share it).
4. Sync Gradle, then run **Build > Rebuild Project**.

The dashboard overview counts are still placeholders (`—`) because live dashboard aggregates have not yet been wired to the database. The Supabase connection check remains in place.

## Profile fix pass
- Profile logo now uses the same navigation as the bottom bar (`navigateToTopLevel`), so Home works afterwards.
- Profile screen uses one scroll container (removed nested LazyColumns that blocked scrolling).
- Redesigned "My Profile" header, user ID card, quick actions, memberships/permissions and college list.
- Bottom bar icons changed from letters to emoji.
