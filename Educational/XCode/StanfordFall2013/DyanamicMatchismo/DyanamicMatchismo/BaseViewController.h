//
//  BaseViewController.h
//  DyanamicMatchismo
//
//  Created by Ethan Petuchowski on 12/19/13.
//  Copyright (c) 2013 Ethan Petuchowski. All rights reserved.
//

/* UIKit has UIButton and UI* and a bunch of NS* */
#import <UIKit/UIKit.h>
#import "MatchingGame.h"
#import "Deck.h"
#import "Grid.h"


@interface BaseViewController : UIViewController

@property (strong, nonatomic) Grid *grid;
@property (strong, nonatomic) MatchingGame *game;

/* to attach a new UILabel, you MUST drag a NEW Label object from the Object Pane.
 * You cannot reuse an old one because the old one is linked to a different var_name.
 * That link was created when you ctrl-dragged into the @property that it used to
 *   be connected to */
@property (weak, nonatomic) IBOutlet UILabel *scoreLabel;
@property (weak, nonatomic) IBOutlet UIButton *redealButton;
@property (weak, nonatomic) IBOutlet UIView *layoutContainerView;

/* Is having `strong` creating a reference cycle here?
 * I don't think so because `CardView`s have explicitly
 * only a weak reference back to this ViewController
 */
@property (strong, nonatomic) NSMutableArray *cardsInView;
/* note there's no `IBOutlet` specification.
 * that's OK because IBOutlet resolves to nothing,
 * it just tells XCode this is something you can hook into the storyboard,
 * and `cardsInView` are /not/ something you can hook into the storyboard.
 */

- (void)restartGame;
- (void)updateUI;
- (IBAction)touchRedealButton:(UIButton *)sender;
- (void)putCardInPlayAtIndex:(int)index intoViewInRect:(CGRect)rect;
- (void)cardWasFlipped:(Card *)card;

@end
