> ### Notice
> *Before you continue, this is the* **ANDROID** *library. If you have an* **iOS** *device, please go here instead*:
> 
> – https://github.com/danielgindi/Charts
> 
> They might tell you to come back here, if they do, listen to them and ignore this notice.

# How to contribute

Bug-fixes and features often come from users of the MPAndroidChart library and improve it greatly. We want to keep it as easy as possible to contribute changes that improve the experience for users all around the world. There are a few guidelines that we need contributors to follow so that we can have a chance of keeping on top of things.

## Creating Issues

There are two main issue templates, one for bugs and another for feature requests. Please use them! You're issue will be much easier to understand, and bugs easier to fix, if you follow the templates. If your issue doesn't fit into those, just use the generic template.

Search existing [issues] to see if your bug has already been reported or if a feature request already exists. Don't forget to remove `is:open` so you see all the issues! If you find that one already exists, use reactions to show how much you care!

## Making Pull Requests

Careful! If you fail to follow these guidlines, you're pull request may be closed, *even if it's really awesome*. 

  0. **Search** open [pull requests] AND existing [issues] to make sure what you want to do isn't already being worked on or already has an open pull request.
  1. **Fork** the repository
  1. **Create** a new branch based on `master`, and name it according to your changes
  1. **Add** your commits, they MUST follow the [Commit Style](#commit-style) below
  1. **Test** your changes by actually running the example app, or create a new example
  1. **Create** a pull request, following the auto-generated template
  1. ???
  1. Profit :money_with_wings:
  
You are encouraged to use [GitHub Desktop] to inspect your code changes before committing them. It can reveal small changes that might have gone unnoticed, and would be requested for removal before merging.

Check out [#3975](https://github.com/PhilJay/MPAndroidChart/pull/3975) for an example of a good-made-better pull request.

## Commit Style

  * **Make commits of logical units**  
  Don't load your commits with tons of changes, this makes it hard to follow what is happening. However, if you have done a lot of work, and there are commits and merges all over the place, squash them down into fewer commits.
  
  * **Conform to the code style**  
  It's easy, just look around!
  
  * **Write good commit messages**  
  You may prefer [Tim Pope's style], you might like the [commitizen-friendly] way. Regardless of the color you pick, you MUST stay within the lines!  
  ```
The commit title CANNOT exceed 50 characters

The body of the message comes after an empty new line, and describes the
changes more thoroughly. If the change is obvious and self-explanatory
from the title, you can omit the body. You should describe all changes
if many were made, or maybe some trickery that only code wizards can
understand.

Be polite and wrap your lines to 72 characters, but if you prefer going
to 100 characters then I guess we can't stop you.
```

## Final Notes

Thanks for reading the contributing file! Have some cake! :cake:

[issues]: https://github.com/PhilJay/MPAndroidChart/issues
[pull requests]: https://github.com/PhilJay/MPAndroidChart/pulls
[GitHub Desktop]: https://desktop.github.com/
[Tim Pope's style]: https://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html
[commitizen-friendly]: https://github.com/commitizen/cz-cli
