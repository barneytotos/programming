//
//  BaseViewController.m
//  DyanamicMatchismo
//
//  Created by Ethan Petuchowski on 12/19/13.
//  Copyright (c) 2013 Ethan Petuchowski. All rights reserved.
//

#import "BaseViewController.h"
#import "CardView.h"

@interface BaseViewController ()

@end

@implementation BaseViewController

- (NSMutableDictionary *)cardsInView
{
    if (!_cardsInView) _cardsInView = [[NSMutableDictionary alloc] init];
    return _cardsInView;
}

- (Grid *)grid
{
    if (!_grid) _grid = [[Grid alloc] init];
    return _grid;
}

- (void)restartGame
{
    self.game = nil;
    [self redrawAllCards];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation
{
    [self redrawAllCards];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if (![self.layoutContainerView.subviews count])
        [self drawAllCards];
}

- (IBAction)touchRedealButton:(UIButton *)sender {
    [self restartGame];
}

// Note: inheriting classes must specify the actual CardView they want to use
- (void)putCardInViewAtIndex:(int)index intoViewInRect:(CGRect)rect
{
    Card *card = self.game.cardsInPlay[index];
    [self.cardsInView setObject:[[CardView alloc]
                                 initWithFrame:rect withCard:card inContainer:self]
                         forKey:card.attributedContents];
}

- (void)redrawAllCards
{
    for (NSString *cardName in [self.cardsInView copy])
        [self removeCardFromView:cardName];
    
    [self drawAllCards];
}

- (void)resetGridSize
{
    [self.grid setCellAspectRatio:1.3/2.0];
    [self.grid setSize:CGSizeMake(self.layoutContainerView.bounds.size.width,
                                  self.layoutContainerView.bounds.size.height)];
    [self.grid setMinimumNumberOfCells:[self.game.cardsInPlay count]];
}

// doesn't check that these cards aren't already in the view
- (void)drawAllCards
{
    [self resetGridSize];
    for (Card *card in self.game.cardsInPlay)
        [self addCardToView:card];
    [self updateUI];
}

- (void)removeCardFromView:(NSString *)cardName
{
    CardView *cardView = [self.cardsInView objectForKey:cardName];
    [cardView animateCardRemoval];
    [self.cardsInView removeObjectForKey:cardName];
}

- (void)addCardToView:(Card *)card
{
    int row = [self.cardsInView count] / self.grid.columnCount;
    int col = [self.cardsInView count] % self.grid.columnCount;
    CGRect rect = CGRectOffset([self.grid frameOfCellAtRow:row inColumn:col], 0.9, 0.9);
    [self putCardInViewAtIndex:[self.cardsInView count] intoViewInRect:rect];
    CardView *brandNewCardView = [self.cardsInView objectForKey:card.attributedContents];
    [self.layoutContainerView addSubview:brandNewCardView];
    [brandNewCardView animateCardInsertion];
}

- (void)updateUI
{
    /* update which cards are "chosen" or "matched" */
    NSMutableDictionary *viewDictCopy = [self.cardsInView mutableCopy];
    for (Card *card in self.game.cards) {
        CardView *cardView = [viewDictCopy objectForKey:card.attributedContents];
        if (card.chosen != cardView.thinksItsChosen) {
            cardView.thinksItsChosen = card.chosen;
            [cardView animateChooseCard];
        }
        if (card.isMatched != cardView.thinksItsMatched) {
            cardView.thinksItsMatched = card.isMatched;
            [self removeCardFromView:(NSString *)card.attributedContents];
        }
        [viewDictCopy removeObjectForKey:card.attributedContents];
    }
    
    // remove cards that have been matched
    for (NSString *cardName in viewDictCopy.allKeys)
        [self removeCardFromView:cardName];
    
    
    // update score
    self.scoreLabel.text = [NSString stringWithFormat:@"Score: %d", self.game.score];

    // relayout the grid of cards if need-be
    if ([self.cardsInView count] <= ([self.grid rowCount] - 1) * ([self.grid columnCount] - 1))
        [self redrawAllCards];
    
    if ([self.cardsInView count] > [self.grid rowCount] * [self.grid columnCount])
        [self redrawAllCards];
}

- (void)cardWasChosen:(Card *)card
{
    [self.game chooseCardAtIndex:[self.game.cards indexOfObject:card]];
    [self updateUI];
}

- (NSString *)titleForCard:(Card *)card
{
    return card.isChosen ? card.contents : @"";
}

- (UIImage *)backgroundImageForCard:(Card *)card
{
    return [UIImage imageNamed:card.isChosen ? @"cardfront" : @"cardback"];
}

@end
